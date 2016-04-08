package com.mishu.cgwy.product.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.controller.SkuCandidatesRequest;
import com.mishu.cgwy.product.controller.SkuListRequest;
import com.mishu.cgwy.product.controller.SkuVendorRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.service.SkuPriceHistoryService;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuVendorWrapper;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.purchase.service.PurchaseOrderItemService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.service.StockInService;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.stock.service.StockService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.task.service.AsyncTask;
import com.mishu.cgwy.task.service.AsyncTaskService;
import com.mishu.cgwy.task.service.TaskResult;
import com.mishu.cgwy.task.util.ExcelUtils;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class SkuFacade {

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuVendorService skuVendorService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SkuPriceHistoryService skuSalePriceLimitHistoryService;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Autowired
    private StockInService stockInService;

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockTotalService stockTotalService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    private Logger logger = LoggerFactory.getLogger(SkuFacade.class);

    @Transactional
    public QueryResponse<SkuVendorWrapper> getSkuList(SkuListRequest request) {

        List<SkuVendorWrapper> list = new ArrayList<SkuVendorWrapper>();

        if (request.getVendorId() == null) {
            Page<Sku> page = skuService.getSkuList(request);

            for (Sku sku : page.getContent()) {
                List<SkuVendor> skuVendors = skuVendorService.findByCityIdAndSkuId(request.getCityId(), sku.getId());

                if (CollectionUtils.isNotEmpty(skuVendors)) {
                    SkuVendor skuVendor = skuVendors.get(0);
                    Vendor vendor = skuVendor.getVendor();
                    SkuVendorWrapper skuVendorWrapper = new SkuVendorWrapper(skuVendor);
                    VendorVo vendorVo = new VendorVo();
                    vendorVo.setId(vendor.getId());
                    vendorVo.setName(vendor.getName());
                    skuVendorWrapper.setVendor(vendorVo);
                    list.add(skuVendorWrapper);
                } else {
                    list.add(new SkuVendorWrapper(sku));
                }
            }
            QueryResponse<SkuVendorWrapper> res = new QueryResponse<SkuVendorWrapper>();
            res.setContent(list);
            res.setPage(request.getPage());
            res.setPageSize(request.getPageSize());
            res.setTotal(page.getTotalElements());
            return res;
        } else {
            Page<SkuVendor> page = skuVendorService.getSkuVendorList(request);
            for (SkuVendor skuVendor : page.getContent()) {
                list.add(new SkuVendorWrapper(skuVendor));
            }
            QueryResponse<SkuVendorWrapper> res = new QueryResponse<SkuVendorWrapper>();
            res.setContent(list);
            res.setPage(request.getPage());
            res.setPageSize(request.getPageSize());
            res.setTotal(page.getTotalElements());
            return res;
        }
    }

    @Transactional
    public void updateSkuVendor(SkuVendorRequest request, AdminUser operator) {
        Long cityId = request.getCityId();
        Long skuId = request.getSkuId();
        Long vendorId = request.getVendorId();
        Sku sku = skuService.getOne(skuId);
        City city = locationService.getCity(cityId);
        Vendor vendor = vendorService.getVendorById(vendorId);

        if (request.getSkuVendorId() != null) {//修改sku和供应商的对应关系
            SkuVendor skuVendor = skuVendorService.getOne(request.getSkuVendorId());
            skuVendor.setVendor(vendor);
            skuVendorService.save(skuVendor);
        } else {//新增sku和供应商的对应关系
            SkuVendor skuVendor = new SkuVendor();
            skuVendor.setSku(sku);
            skuVendor.setVendor(vendor);
            skuVendor.setCity(city);
            skuVendorService.save(skuVendor);
        }

        updatePrice(request, operator, city, sku, vendor);
    }

    @Transactional
    public void batchUpdateSkuPrice(AdminUser adminUser, Long cityId, Long skuId, BigDecimal fixedPrice, BigDecimal singleSalePriceLimit, BigDecimal bundleSalePriceLimit) {
        Sku sku = skuService.getOne(skuId);
        City city = locationService.getCity(cityId);
        SkuVendorRequest request = new SkuVendorRequest();
        request.setCityId(cityId);
        request.setSkuId(skuId);
        request.setFixedPrice(fixedPrice);
        request.setSingleSalePriceLimit(singleSalePriceLimit);
        request.setBundleSalePriceLimit(bundleSalePriceLimit);
        updatePrice(request, adminUser, city, sku, null);
    }

    @Transactional
    private void updatePrice(SkuVendorRequest request, AdminUser operator, City city, Sku sku, Vendor vendor) {
        BigDecimal currentFixedPrice = request.getFixedPrice().setScale(6, RoundingMode.HALF_UP);
        BigDecimal currentSingleSalePriceLimit = request.getSingleSalePriceLimit().setScale(6, RoundingMode.HALF_UP);
        BigDecimal currentBundleSalePriceLimit = request.getBundleSalePriceLimit().setScale(6, RoundingMode.HALF_UP);
        SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(city.getId(), sku.getId());

        if (new BigDecimal(0).compareTo(currentFixedPrice) != 0) {//定价不为0才记录
            boolean logSkuPrice = false;
                if (skuPrice == null) {//不存在sku的价格记录，则新增
                logSkuPrice = true;//第一次录入定价，记录价格历史
                skuPrice = new SkuPrice();
                skuPrice.setSku(sku);
                skuPrice.setCity(city);
                skuPrice.setOldFixedPrice(BigDecimal.ZERO);
                skuPrice.setFixedPrice(currentFixedPrice);
                skuPriceService.save(skuPrice);
            } else if (skuPrice.getFixedPrice() == null || currentFixedPrice.compareTo(skuPrice.getFixedPrice()) != 0) {//存在sku的价格记录，且定价发生变化，则变更
                logSkuPrice = true;//第一次录入定价，或者价格发生变化
                skuPrice.setOldFixedPrice(skuPrice.getFixedPrice());
                skuPrice.setFixedPrice(currentFixedPrice);
                skuPriceService.save(skuPrice);
            }

            if (logSkuPrice) {
                SkuPriceHistory history = new SkuPriceHistory();
                history.setSku(sku);
                history.setVendor(vendor);
                history.setCity(city);
                history.setCreateDate(new Date());
                history.setOperator(operator);
                history.setFixedPrice(currentFixedPrice);
                history.setReason(request.getChangeFixedPriceReason());
                history.setType(SkuPriceType.FIXED_PRICE.getValue());
                skuSalePriceLimitHistoryService.save(history);
            }
        }

        if (BigDecimal.ZERO.compareTo(currentSingleSalePriceLimit) < 0 || BigDecimal.ZERO.compareTo(currentBundleSalePriceLimit) < 0) {//定价不为0才记录
            boolean logSkuPrice = false;
            if (skuPrice == null) {//不存在sku的价格记录，则新增
                logSkuPrice = true;//第一次录入销售限价，记录价格历史
                skuPrice = new SkuPrice();
                skuPrice.setSku(sku);
                skuPrice.setCity(city);
                skuPrice.setOldSingleSalePriceLimit(BigDecimal.ZERO);
                skuPrice.setOldBundleSalePriceLimit(BigDecimal.ZERO);
                skuPrice.setSingleSalePriceLimit(currentSingleSalePriceLimit);
                skuPrice.setBundleSalePriceLimit(currentBundleSalePriceLimit);
                skuPriceService.save(skuPrice);
            } else if (skuPrice.getSingleSalePriceLimit() == null || currentSingleSalePriceLimit.compareTo(skuPrice.getSingleSalePriceLimit()) != 0 || skuPrice.getBundleSalePriceLimit() == null || currentBundleSalePriceLimit.compareTo(skuPrice.getBundleSalePriceLimit()) != 0) {//存在sku的价格记录，且定价发生变化，则变更
                logSkuPrice = true;//第一次录入定价，或者价格发生变化
                skuPrice.setOldSingleSalePriceLimit(skuPrice.getSingleSalePriceLimit());
                skuPrice.setOldBundleSalePriceLimit(skuPrice.getBundleSalePriceLimit());
                skuPrice.setSingleSalePriceLimit(currentSingleSalePriceLimit);
                skuPrice.setBundleSalePriceLimit(currentBundleSalePriceLimit);
                skuPriceService.save(skuPrice);
            }

            if (logSkuPrice) {
                SkuPriceHistory history = new SkuPriceHistory();
                history.setSku(sku);
                history.setVendor(vendor);
                history.setCity(city);
                history.setCreateDate(new Date());
                history.setOperator(operator);
                history.setSingleSalePriceLimit(currentSingleSalePriceLimit);
                history.setBundleSalePriceLimit(currentBundleSalePriceLimit);
                history.setReason(request.getChangeSalePriceLimitReason());
                history.setType(SkuPriceType.SALE_PRICE_LIMIT.getValue());
                skuSalePriceLimitHistoryService.save(history);
            }
        }
    }

    public List<CandidateSkuWrapper> getSkuCandidates(SkuCandidatesRequest request) {
        return skuService.getSkuCandidates(request);
    }

    public SkuVendorWrapper getSkuVendor(Long id) {
        SkuVendor skuVendor = skuVendorService.getOne(id);
        Vendor vendor = skuVendor.getVendor();
        SkuVendorWrapper wrapper = new SkuVendorWrapper(skuVendor);
        VendorVo vendorVo = new VendorVo();
        vendorVo.setId(vendor.getId());
        vendorVo.setName(vendor.getName());

        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(vendor.getOrganization().getId());
        vendorVo.setOrganization(organizationVo);

        wrapper.setVendor(vendorVo);

        Long cityId = skuVendor.getCity().getId();
        Long skuId = skuVendor.getSku().getId();
        SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(cityId, skuId);

        wrapper.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
        wrapper.setSingleSalePriceLimit(skuPrice == null || skuPrice.getSingleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getSingleSalePriceLimit());
        wrapper.setBundleSalePriceLimit(skuPrice == null || skuPrice.getBundleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getBundleSalePriceLimit());

        return wrapper;
    }

    public void updatePurchasePrice(StockIn stockIn) {
        Vendor vendor = stockIn.getPurchaseOrder().getVendor();
        City city = vendor.getCity();
        for (StockInItem stockInItem : stockIn.getStockInItems()) {
            Integer quantity = stockInItem.getRealQuantity();
            if (!new Integer(0).equals(quantity)) {
                Sku sku = stockInItem.getSku();
                BigDecimal price = stockInItem.getPrice();
                SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(city.getId(), sku.getId());
                boolean logSkuPrice = false;
                if (skuPrice == null) {//不存在sku的价格记录，则新增
                    logSkuPrice = true;//第一次录入定价，记录价格历史
                    skuPrice = new SkuPrice();
                    skuPrice.setSku(sku);
                    skuPrice.setCity(city);
                    skuPrice.setOldPurchasePrice(BigDecimal.ZERO);
                    skuPrice.setPurchasePrice(price);
                    skuPriceService.save(skuPrice);
                } else if (skuPrice.getPurchasePrice() == null || skuPrice.getPurchasePrice().compareTo(price) != 0) {//存在sku的价格记录，且定价发生变化，则变更
                    logSkuPrice = true;//第一次录入定价，或者价格发生变化
                    skuPrice.setOldPurchasePrice(skuPrice.getPurchasePrice());
                    skuPrice.setPurchasePrice(price);
                    skuPriceService.save(skuPrice);
                }

                if (logSkuPrice) {
                    AdminUser operator = null;
                    PurchaseOrder purchaseOrder = stockIn.getPurchaseOrder();
                    if (PurchaseOrderType.STOCKUP.getValue().equals(purchaseOrder.getType())) {
                        operator = purchaseOrder.getCreater();
                    } else if (PurchaseOrderType.ACCORDING.getValue().equals(purchaseOrder.getType())) {
                        operator = purchaseOrder.getCutOrder().getSubmitUser();
                    }

                    SkuPriceHistory history = new SkuPriceHistory();
                    history.setSku(sku);
                    history.setVendor(vendor);
                    history.setCity(city);
                    history.setCreateDate(new Date());
                    history.setOperator(operator);
                    history.setPurchasePrice(price);
                    history.setType(SkuPriceType.PURCHASE_PRICE.getValue());
                    skuSalePriceLimitHistoryService.save(history);
                }
            }
        }
    }

    public void updateSkuCapacityInBundle(Long skuId, Integer capacityInBundle) {
        Sku sku = skuService.findOne(skuId);
        if (sku == null || sku.getId() == null) {
            throw new UserDefinedException("sku不存在");
        }
        List<StockTotal> stockTotals = stockTotalService.findStockTotalBySkuId(skuId);
        if (!stockTotals.isEmpty()) {
            throw new UserDefinedException("库存不为空");
        }
        List<Stock> stocks = stockService.findStockBySkuId(skuId);
        if (!stocks.isEmpty()) {
            throw new UserDefinedException("库存不为空");
        }
        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemService.findBySkuId(skuId);
        if (!purchaseOrderItems.isEmpty()) {
            throw new UserDefinedException("外采单不为空");
        }
        List<StockInItem> stockInItems = stockInService.findStockInItemBySkuId(skuId);
        if (!stockInItems.isEmpty()) {
            throw new UserDefinedException("入库单不为空");
        }
        List<StockOutItem> stockOutItems = stockOutService.findStockOutItemBySkuId(skuId);
        if (!stockOutItems.isEmpty()) {
            throw new UserDefinedException("出库单不为空");
        }
        sku.setCapacityInBundle(capacityInBundle);
        skuService.saveSku(sku);
    }

    public String skuPriceExcelImport(MultipartFile file, Long cityId, AdminUser adminUser) {
        try {
            Workbook wb = null;
            try {
                wb = new XSSFWorkbook(file.getInputStream());
            } catch (Exception e) {
                wb = new HSSFWorkbook(file.getInputStream());
            }

            Sheet sheet = wb.getSheetAt(0);

            String result = null;
            List<List> values = new ArrayList<>();
            for (int line = 1; line < sheet.getPhysicalNumberOfRows(); line++) {
                Row row = sheet.getRow(line);
                int index = 0;
                String skuIdStr = ExcelUtils.getStringValue(row.getCell(index++));
                String fixedPriceStr = ExcelUtils.getStringValue(row.getCell(index++));
                String singleSalePriceLimitStr = ExcelUtils.getStringValue(row.getCell(index++));
                String bundleSalePriceLimitStr = ExcelUtils.getStringValue(row.getCell(index++));

                Long skuId = null;
                try {
                    skuId = Float.valueOf(skuIdStr).longValue();
                } catch (Exception e) {
                    result = String.format("第%s行skuId非数字", line + 1);
                    break;
                }

                BigDecimal fixedPrice = null;
                if (StringUtils.isNotBlank(fixedPriceStr)) {
                    try {
                        fixedPrice = new BigDecimal(fixedPriceStr);
                    } catch (Exception e) {
                        result = String.format("第%s行定价非法", line + 1);
                        break;
                    }
                }

                BigDecimal singleSalePriceLimit = null;
                if (StringUtils.isNotBlank(singleSalePriceLimitStr)) {
                    try {
                        singleSalePriceLimit = new BigDecimal(singleSalePriceLimitStr);
                    } catch (Exception e) {
                        result = String.format("第%s行单品限价非法", line + 1);
                        break;
                    }
                }

                BigDecimal bundleSalePriceLimit = null;
                if (StringUtils.isNotBlank(bundleSalePriceLimitStr)) {
                    try {
                        bundleSalePriceLimit = new BigDecimal(bundleSalePriceLimitStr);
                    } catch (Exception e) {
                        result = String.format("第%s行打包限价非法", line + 1);
                        break;
                    }
                }

                SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(cityId, skuId);

                if (fixedPrice == null) {
                    fixedPrice = NumberUtils.cancelNull(skuPrice == null ? null : skuPrice.getFixedPrice());
                }

                if (singleSalePriceLimit == null) {
                    singleSalePriceLimit = NumberUtils.cancelNull(skuPrice == null ? null : skuPrice.getSingleSalePriceLimit());
                }

                if (bundleSalePriceLimit == null) {
                    bundleSalePriceLimit = NumberUtils.cancelNull(skuPrice == null ? null : skuPrice.getBundleSalePriceLimit());
                }

                List value = new ArrayList();
                value.add(skuId);
                value.add(fixedPrice);
                value.add(singleSalePriceLimit);
                value.add(bundleSalePriceLimit);
                values.add(value);

            }

            if (result != null) {
                return result;
            }

            for (List value : values) {
                batchUpdateSkuPrice(adminUser, cityId, (Long) value.get(0), (BigDecimal) value.get(1), (BigDecimal) value.get(2), (BigDecimal) value.get(3));
            }

            return "导入成功";
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "导入失败，请联系技术人员";
        }
    }

    public void asyncSkuPriceExcelImport(final MultipartFile file, final Long cityId, final AdminUser adminUser) throws Exception {
        asyncTaskService.excelImport(
                adminUser,
                "sku定价/限价导入",
                new AsyncTask() {
                    @Override
                    public TaskResult proceed() throws Exception {
                        TaskResult taskResult = new TaskResult();
                        taskResult.setRemark(skuPriceExcelImport(file, cityId, adminUser));
                        return taskResult;
                    }
                }
        );
    }

    public HttpEntity<byte[]> downloadTemplate() {
        return ExportExcelUtils.download(ExportExcelUtils.SKU_PRICE_IMPORT_TEMPLATE);
    }


    public List<SkuVo> getSkus(List<Long> skuIds) {

        List<Sku> skus = this.skuService.getSkuList(skuIds);

        Collection<SkuVo> datas = Collections2.transform(skus, new Function<Sku, SkuVo>() {
            @Override
            public SkuVo apply(Sku input) {
                SkuVo svo = new SkuVo();
                svo.setId(input.getId());
                svo.setName(input.getName());
                return svo;
            }
        });

        return new ArrayList<>(datas);
    }
}
