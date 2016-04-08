package com.mishu.cgwy.product.facade;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.Refund;
import com.mishu.cgwy.order.repository.OrderGroupRepository;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.EdbSkuRepository;
import com.mishu.cgwy.product.service.EdbSkuService;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.NumberUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author wangwei
 *         Date : 2015/06/15
 *         Time : 14:29
 */
@Service
public class ProductEdbFacade {

    private static Logger logger = LoggerFactory.getLogger(ProductEdbFacade.class);

    public static final String appkey = "4bc96674";
    protected static String testUrl = "http://vip58.edb07.com/rest/index.aspx";
    public static final String dbhost = "edb_a81745";
    public static final String secret = "9fd42adbaef34adc81980de2ca0ad1f1";
    public static final String token = "11950316ce7a4212859d30387233b7b3";

    public static final String ip = "192.168.60.80";

    // 返回格式
    public static final String format = "json";

    @Autowired(required = false)
    private ContextualInventoryService contextualInventoryService;
    @Autowired
    private EdbSkuRepository edbSkuRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private EdbSkuService edbSkuService;

	@Autowired
	private OrderGroupRepository orderGroupRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ContextualInventoryService inventoryService;

    //condition	 市场1 城北市场， 仓库id  13
//    Long warehouseId = 1L;
//    Long vendorId = 13L;

    public Map<Sku, Integer> getSalesByWarehouseId(Date expectedArrivedDate, Long warehouseId) {
        Map<Sku, Integer> map = new HashMap<Sku, Integer>();
        for (OrderGroup orderGroup : orderService.getOrderGroupOfEdbByWarehouseId(expectedArrivedDate, warehouseId)) {
            for (Order order : orderGroup.getMembers()) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    add(map, orderItem.getSku(), orderItem.getCountQuantity());
                }
                for (Promotion promotion : order.getPromotions()) {
                    add(map, promotion.getPromotableItems().getSku(), promotion.getPromotableItems().getQuantity());
                }
            }
        }
        return map;
    }

    public Map<Sku, Integer> getEdbStock(Date stockDate) {
        Map<Sku, Integer> map = new HashMap<Sku, Integer>();
        List<EdbSku> edbSkus = edbSkuService.findByStockDate(stockDate);
        for (EdbSku edbSku : edbSkus) {
            map.put(edbSku.getSku(), edbSku.getStock());
        }
        return map;
    }

    @Data
    class Purchase {
        private Sku sku;
        private int stock;
        private int cbTotal;
        private int wxyTotal;
        private int trans;
        private int cbPurchase;
        private int wxyPurchase;
        private int totalPurchase;
        private Vendor vendor;

        public Purchase(Sku sku, int stock, int cbTotal, int wxyTotal, int trans, int cbPurchase, int wxyPurchase, int totalPurchase, Vendor vendor) {
            this.sku = sku;
            this.stock = stock;
            this.cbTotal = cbTotal;
            this.wxyTotal = wxyTotal;
            this.trans = trans;
            this.cbPurchase = cbPurchase;
            this.wxyPurchase = wxyPurchase;
            this.totalPurchase = totalPurchase;
            this.vendor = vendor;
        }
    }

    public File generatePurchaseExcel(Date expectedArrivedDate) throws Exception {

        List<Purchase> purchases = new ArrayList<>();

        Map<Sku, Integer> cbMap = getSalesByWarehouseId(expectedArrivedDate, 1l);
        Map<Sku, Integer> wxyMap = getSalesByWarehouseId(expectedArrivedDate, 3l);
        Map<Sku, Integer> stockMap = getEdbStock(expectedArrivedDate);

        for (Object key : CollectionUtils.union(cbMap.keySet(), wxyMap.keySet())) {
            Sku sku = (Sku)key;
            int cbTotal = NumberUtils.intValue(cbMap.get(sku));
            int wxyTotal = NumberUtils.intValue(wxyMap.get(sku));
            int stock = NumberUtils.intValue(stockMap.get(sku));

            boolean wsyEnough = stock >= wxyTotal;
            int trans = wsyEnough ? wxyTotal : (stock >=0 ? stock : 0);
            int wsyPurchase = wsyEnough ? 0 : wxyTotal - (stock >=0 ? stock : 0);
            int remained = wsyEnough ? stock - wxyTotal : (stock >=0 ? 0 : stock);
            int cbPurchase = remained >= cbTotal ? 0 : cbTotal - remained;
            int totalPurchase = wsyPurchase + cbPurchase;

            DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPrice(sku.getId(), 1l);
            purchases.add(new Purchase(sku, stock, cbTotal, wxyTotal, trans, cbPurchase, wsyPurchase, totalPurchase, null));
        }

        return generatePurchaseExcel(purchases);
    }

    private void generatePurchaseExcel(HSSFSheet sheet, List<List<String>> sheetList) {
        if (CollectionUtils.isNotEmpty(sheetList)) {
            for (int i = 0; i < sheetList.size(); i++) {
                List<String> line = sheetList.get(i);
                HSSFRow row = sheet.createRow(i);
                for (int j = 0; j < line.size(); j++) {
                    HSSFCell cell = row.createCell(j);
                    cell.setCellValue(line.get(j));
                }
            }
        }
    }

    public File generatePurchaseExcel(List<Purchase> purchases) throws Exception {
        File file = new File(ExportExcelUtils.excelFolderName, "purchase-excel.xls");

        FileOutputStream out = new FileOutputStream(file);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        List<List<String>> sheetList = new ArrayList<>();
        List<String> headRow = new ArrayList<>();
        headRow.add("SKU_ID");
        headRow.add("SKU名称");
        headRow.add("现有库存");
        headRow.add("城北总销量");
        headRow.add("王四营总销量");
        headRow.add("城北仓库调拨量");
        headRow.add("城北需购买量");
        headRow.add("王四营需购买量");
        headRow.add("总购买量");
        headRow.add("供货商");

        sheetList.add(headRow);

        for (int i = 0; i < purchases.size(); i++) {
            Purchase purchase = purchases.get(i);
            List<String> sheetRow = new ArrayList<>();
            sheetRow.add(String.valueOf(purchase.getSku().getId()));
            sheetRow.add(purchase.getSku().getName());
            sheetRow.add(String.valueOf(purchase.getStock()));
            sheetRow.add(String.valueOf(purchase.getCbTotal()));
            sheetRow.add(String.valueOf(purchase.getWxyTotal()));
            sheetRow.add(String.valueOf(purchase.getTrans()));
            sheetRow.add(String.valueOf(purchase.getCbPurchase()));
            sheetRow.add(String.valueOf(purchase.getWxyPurchase()));
            sheetRow.add(String.valueOf(purchase.getTotalPurchase()));
            sheetRow.add(purchase.getVendor() == null ? null : purchase.getVendor().getName());

            sheetList.add(sheetRow);
        }

        generatePurchaseExcel(sheet, sheetList);

        try {
            wb.write(out);
        } finally {
            out.close();
        }

        return file;
    }

    public void add(Map<Sku, Integer> map, Sku sku, int quantity) {
        Integer total = map.get(sku);
        map.put(sku, total == null ? quantity : quantity + total);
    }

    private void addItem(Map<Long, List<Map<String, Object>>> listMap, String outTid, Sku sku, BigDecimal costPrice, BigDecimal outPrice, int quantity) {
        if (listMap.get(sku.getId()) == null) {
            listMap.put(sku.getId(), new ArrayList<Map<String, Object>>());
        }

        List<Map<String, Object>> list = listMap.get(sku.getId());

        boolean exist = Boolean.FALSE;
        if (!list.isEmpty()) {
            for (Map<String, Object> map : list) {
                if (map.get("cost_Price").equals(costPrice)) {
                    exist = Boolean.TRUE;
                    Integer num = Integer.valueOf(map.get("orderGoods_Num").toString());
                    map.put("orderGoods_Num", quantity + num);
                    break;
                }
            }
        }

        if (!exist) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("barCode", sku.getId());
            map.put("product_title", sku.getProduct().getName());
            map.put("standard", 1);
            map.put("out_tid", outTid);
            map.put("orderGoods_Num", quantity);
            //外部单价
            map.put("out_price", outPrice);
            //成交单价
            map.put("cost_Price", costPrice);
            list.add(map);
        }
    }

    public boolean getProduct(Sku sku) {

        TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();

        apiparamsMap.put("method", "edbProductBaseInfoGet");//添加请求参数——接口名称
        apiparamsMap.put("barCode", String.valueOf(sku.getId()));

//        apiparamsMap.put("productName", product.getName());
//        apiparamsMap.put("productName", "李锦记锦珍老抽1.75L*6桶/箱");

        String xml = getConnectXml(apiparamsMap);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(xml);
            JSONObject Success = jsonObject.getJSONObject("Success");
            Long total_results = Success.getLong("total_results");
            if (total_results > 0) {
                return true;
            }
            return false;
        } catch (JSONException e) {
        }
        return false;
    }

    @Transactional
    public void synchroEdbData(Date date) {
        try {
            TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();

            apiparamsMap.put("method", "edbProductGet");
            apiparamsMap.put("isuit", "-1");
            apiparamsMap.put("page_size", "10000");
            apiparamsMap.put("store_id", "1,3");

            for (int pageNo = 1; pageNo <= 10; pageNo++) {
                apiparamsMap.put("page_no", String.valueOf(pageNo));
                String json = getConnectXml(apiparamsMap);

                JSONObject jsonObject = new JSONObject(json);
                JSONObject success = jsonObject.getJSONObject("Success");
                JSONObject items = success.getJSONObject("items");
                JSONArray item = items.getJSONArray("item");

                for (int i = 0; i < item.length(); i++) {
                    JSONObject object = item.getJSONObject(i);
                    EdbSku edbSku = new EdbSku();
                    edbSku.setStock(NumberUtils.toInt(object.getString("entity_stock")));
                    edbSku.setAvgPrice(NumberUtils.createBigDecimal(object.getString("cost")));
                    edbSku.setStockDate(DateUtils.truncate(date, Calendar.DATE));

                    Sku sku = productService.findSku(Long.parseLong(object.getString("product_no")));
                    if (sku == null) {
                        continue;
                    }
                    edbSku.setSku(sku);
                    edbSkuService.saveEdbSku(edbSku);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String updateProduct(Sku sku, Long warehouseId) {
        return updateProduct(contextualInventoryService.getDynamicSkuPrice(sku.getId(), warehouseId));
    }


    public String updateProduct(Long dynamicSkuPriceId) {
        return updateProduct(contextualInventoryService.getDynamicSkuPriceById(dynamicSkuPriceId));
    }

    // not really update Product
    public String updateProduct(DynamicSkuPrice dynamicSkuPrice) {
        Sku sku = dynamicSkuPrice.getSku();
        if (!getProduct(sku)) {
            String msg = saveProduct(dynamicSkuPrice);
            if (StringUtils.isNotBlank(msg)) {
                return msg;
            }
        }
        return null;
    }


    public String saveProduct(Sku sku, Long warehouseId) {
        return saveProduct(contextualInventoryService.getDynamicSkuPrice(sku.getId(), warehouseId));
    }

    public String saveProduct(DynamicSkuPrice dynamicSkuPrice) {
        if (!dynamicSkuPrice.getWarehouse().getId().equals(1L) && !dynamicSkuPrice.getWarehouse().getId().equals(3L) && !dynamicSkuPrice.getWarehouse().getId().equals(4L)) {
            return null;
        }
        Sku sku = dynamicSkuPrice.getSku();
        Product product = sku.getProduct();
        TreeMap<String, Object> orderInfo = new TreeMap<>();
        orderInfo.put("brand_name", "未知品牌");
        orderInfo.put("sort_name", "未知分类");
        orderInfo.put("supplier", "未知供应商");
        orderInfo.put("productNo", sku.getId());
        orderInfo.put("product_name", product.getName());
        orderInfo.put("market_price", sku.getMarketPrice());
        orderInfo.put("retail_price", dynamicSkuPrice.getSinglePriceStatus() != null ? dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice() : 0);
//        orderInfo.put("product_intro", StringUtils.isNotBlank(product.getDescription()) ? product.getDescription() : "介绍");
        orderInfo.put("factory_item", sku.getId());
        orderInfo.put("wfpid", 1);

        TreeMap<String, Object> detail_item = new TreeMap<>();
        detail_item.put("bar_code", sku.getId());
        detail_item.put("specification", product.getPropertyMap().get("specification") != null ? product.getPropertyMap().get("specification") : "默认规格");
        detail_item.put("sell_price", dynamicSkuPrice.getSinglePriceStatus() != null ? dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice() : 0);
        detail_item.put("is_consump", 1);
        detail_item.put("product_status", "正常");

        Map<String, Object> detailInfo = new HashMap<String, Object>();
        detailInfo.put("detail_item", detail_item);


        Map<String, Object> order = new HashMap<String, Object>();
        order.put("orderInfo", orderInfo);
        order.put("detailInfo", detailInfo);

        String maptoXml = maptoXml(order, "order");

        TreeMap<String, String> map = new TreeMap<>();
        map.put("method", "edbProductDetailAdd");
        map.put("xmlValues", maptoXml);
        String returnJson = getConnectXml(map);
        try {
            JSONObject jsonObject = new JSONObject(returnJson);
            JSONObject success = jsonObject.getJSONObject("Success");
            JSONObject items = success.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");
            JSONObject object = item.getJSONObject(0);
            String warning = object.getString("warning");
            return warning;
        } catch (JSONException e) {
        }
        return null;
    }

    public String maptoXml(Map map, String root) {
        Document document = DocumentHelper.createDocument();
        Element nodeElement = document.addElement(root);
        mapChildElement(map, nodeElement);
        return nodeElement.asXML();
    }


    private void mapChildElement(Map<String, Object> map, Element nodeElement) {
        if (!map.isEmpty()) {
            Iterator its = map.keySet().iterator();
            while (its.hasNext()) {
                Object key = its.next();
                Object obj = map.get(key);
                if (obj instanceof Map) {
                    Element element = nodeElement.addElement(key.toString());
                    mapChildElement((Map<String, Object>) obj, element);
                } else if (obj instanceof Object[]) {
                    for (Object nobj : (Object[]) obj) {
                        Element element = nodeElement.addElement(key.toString());
                        mapChildElement((Map<String, Object>) nobj, element);
                    }
                } else {
                    Element element = nodeElement.addElement(key.toString());
                    element.setText(obj != null ? obj.toString() : "");
                }
            }
        }
    }


    public String getConnectXml(TreeMap<String, String> map) {
        TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();

        apiparamsMap.put("dbhost", dbhost);//添加请求参数——主帐号

        apiparamsMap.put("format", format);//添加请求参数——返回格式

        apiparamsMap.put("slencry", "0");//添加请求参数——返回结果是否加密（0，为不加密 ，1.加密）

        apiparamsMap.put("ip", ip);//添加请求参数——IP地址

        apiparamsMap.put("appkey", appkey);//添加请求参数——appkey

        apiparamsMap.put("appscret", secret);//添加请求参数——appscret

        apiparamsMap.put("token", token);//添加请求参数——token

        apiparamsMap.put("v", "2.0");//添加请求参数——版本号（目前只提供2.0版本）

        apiparamsMap.put("timestamp", DateFormatUtils.format(new Date(), "yyyyMMddHHmm"));// 添加请求参数——时间戳
//        apiparamsMap.put("timestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));// 添加请求参数——时间戳

        apiparamsMap.putAll(map);

        apiparamsMap.put("sign", md5Signature(apiparamsMap, appkey));

        StringBuilder param = new StringBuilder();

        for (Iterator<Map.Entry<String, String>> it = apiparamsMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Map.Entry<String, String> e = it.next();
            if (e.getKey() != "appscret" && e.getKey() != "token") {
                if (e.getKey().equals("xmlValues") || e.getKey().equals("StartTime")) {
                    try {
                        param.append("&").append(e.getKey()).append("=")
                                .append(encodeUri(e.getValue()));
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    param.append("&").append(e.getKey()).append("=")
                            .append(e.getValue());
                }
            }
        }

        String PostData = param.toString().substring(1);
//        System.out.println(testUrl + "?" + PostData);
        String result = getResult(testUrl, PostData);
//        System.out.println(result);
        logger.info(testUrl + "?" + PostData);
        logger.info(result);

        return result;
    }


    public static String encodeUri(String str)
            throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(str)) {
            str = URLEncoder.encode(str, "UTF-8");
        }
        return str;
    }

    /**
     * 新的md5签名，首尾放secret。
     *
     * @param secret 分配给您的APP_SECRET
     */
    private static String md5Signature(TreeMap<String, String> params,
                                       String secret) {

        String result = null;

        StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));

        if (orgin == null)

            return result;

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));

        } catch (Exception e) {

            throw new java.lang.RuntimeException("sign error !");

        }

        return result;

    }

    /**
     * 二行制转字符串
     */
    private static String byte2hex(byte[] b) {

        StringBuffer hs = new StringBuffer();

        String stmp = "";

        for (int n = 0; n < b.length; n++) {

            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

            if (stmp.length() == 1)

                hs.append("0").append(stmp);

            else

                hs.append(stmp);

        }

        return hs.toString().toUpperCase();

    }

    /**
     * 添加参数的封装方法
     */
    private static StringBuffer getBeforeSign(TreeMap<String, String> params,
                                              StringBuffer orgin) {

        if (params == null)

            return null;

        Map<String, String> treeMap = new TreeMap<String, String>();

        treeMap.putAll(params);

        Iterator<String> iter = treeMap.keySet().iterator();
        while (iter.hasNext()) {

            String name = (String) iter.next();

            orgin.append(name).append(params.get(name));

        }
        return orgin;

    }

    /**
     * 连接到TOP服务器并获取数据
     */
    public static String getResult(String urlStr, String content) {

        URL url = null;

        HttpURLConnection connection = null;

        try {

            url = new URL(urlStr);

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);

            connection.setDoInput(true);

            connection.setRequestMethod("POST");

            connection.setUseCaches(false);

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.connect();

            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());

            out.write(content.getBytes("utf-8"));

            out.flush();

            out.close();

            BufferedReader reader =

                    new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "utf-8"));

            StringBuffer buffer = new StringBuffer();

            String line = "";

            while ((line = reader.readLine()) != null) {

                buffer.append(line);

            }

            reader.close();

            return buffer.toString();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (connection != null) {

                connection.disconnect();

            }

        }

        return null;
    }

    public List<EdbSku> findEdbSkus(Date date) {
        return edbSkuRepository.findByStockDate(date);
    }

	public String addRefund(List<Refund > refunds) throws JSONException {
        Corder corder = new Corder();
        CorderInfo corderInfo = new CorderInfo();
        List<RproductItem> rproductItems = new ArrayList<>();
        Refund defaultRefund = null;
        if (!refunds.isEmpty()) {

            defaultRefund = refunds.get(0);
        }
        List<OrderGroup> orderGroups = orderGroupRepository.findOrderGroupByOrder(defaultRefund.getOrder());

        OrderGroup orderGroup = orderGroups.get(0);

        corderInfo.setWresingnum(orderGroup.getId());
        corderInfo.setStoaffirm("0");
        corderInfo.setRetime(DateFormatUtils.format(defaultRefund.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
        corderInfo.setReturntime(DateFormatUtils.format(defaultRefund.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
        corderInfo.setReoperator("操作员");
        corderInfo.setAuditor("操作员");
        corderInfo.setOrdernum(getOrderNum(orderGroup.getId(), DateUtils.addDays(defaultRefund.getOrder().getSubmitDate(), -1), new Date()));

        for (Refund refund : refunds) {

            RproductItem rproductItem = new RproductItem();
            rproductItem.setBarCode(refund.getSku().getId());
            rproductItem.setCbarcode(refund.getSku().getId());
            rproductItem.setWresingnum(orderGroup.getId());
//            rproductItem.setPronum(Long.valueOf(refund.getQuantity()));
            rproductItem.setReamount(refund.getTotalPrice());
//            rproductItem.setReturnnum(Long.valueOf(refund.getQuantity()));
//            rproductItem.setStoragenum(orderGroup.getWarehouse().getId());
//            rproductItem.setStolocanum(orderGroup.getWarehouse().getId());
            rproductItems.add(rproductItem);

        }
        corder.setCorderInfo(corderInfo);
        corder.setRproductInfo(rproductItems);
        TreeMap<String, String> map = new TreeMap<>();
        try {
            map.put("method", "edbReturnStoreAdd");
            map.put("xmlValues", new XmlMapper().writeValueAsString(corder).replaceFirst("\\s*xmlns=\"\"\\s*", ""));

            String returnJson = getConnectXml(map);
            JSONObject jsonObject = new JSONObject(returnJson);
            JSONObject success = jsonObject.getJSONObject("Success");
            JSONObject items = success.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");
            JSONObject object = item.getJSONObject(0);
            String errCode = object.getString("response_Code");
            return errCode;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }


        return null;
    }
    @Transactional(readOnly = true)
    public String getOrderNum(Long orderGroupId,Date start,Date end) {
        TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();

        apiparamsMap.put("method", "edbTradeGet");
        apiparamsMap.put("out_tid", String.valueOf(orderGroupId));
        apiparamsMap.put("begin_time", DateFormatUtils.format(start, "yyyy-MM-dd HH:mm:ss"));
        apiparamsMap.put("end_time", DateFormatUtils.format(end, "yyyy-MM-dd HH:mm:ss"));

        String json = getConnectXml(apiparamsMap);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject success = jsonObject.getJSONObject("Success");
            JSONObject items = success.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");
            JSONObject object = item.getJSONObject(0);
            String orderNum = object.getString("tid");
            return orderNum;
        } catch (Exception e) {
            logger.info(e.getMessage(),e);
        }
        return null;
    }
}
