package com.mishu.cgwy.purchase.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.order.service.CutOrderService;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.domain.SkuVendor;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.purchase.controller.ChangeSignRequest;
import com.mishu.cgwy.purchase.controller.PurchaseOrderData;
import com.mishu.cgwy.purchase.controller.PurchaseOrderRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.enumeration.PurchaseOrderItemSignStatus;
import com.mishu.cgwy.purchase.service.PurchaseOrderItemService;
import com.mishu.cgwy.purchase.service.PurchaseOrderItemSignService;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.purchase.service.ReturnNoteService;
import com.mishu.cgwy.purchase.vo.*;
import com.mishu.cgwy.purchase.wrapper.SkuInfo;
import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.facade.StockInFacade;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.vendor.service.VendorOrderItemService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PurchaseOrderFacade {

    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderFacade.class);
    @Autowired
    private SkuService skuService;
    @Autowired
    private StockTotalService stockTotalService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private SkuVendorService skuVendorService;
    @Autowired
    private ReturnNoteService returnNoteService;
    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private StockInFacade stockInFacade;

    @Autowired
    private VendorOrderItemService vendorOrderItemService;

    @Autowired
    private CutOrderService cutOrderService;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Autowired
    private PurchaseOrderItemSignService purchaseOrderItemSignService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepotService depotService;

    public SkuInfo getSkuInfo(Long cityId, Long skuId, Integer status) {

        Sku sku = skuService.findOne(skuId);

        if (sku == null) {
            return null;
        }

        if (status != null && !status.equals(sku.getStatus())) {
            return null;
        }

        StockTotal stockTotal = stockTotalService.findStockTotal(cityId, skuId);

        SkuInfo skuInfo = new SkuInfo(sku, stockTotal, null);

        SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(cityId, skuId);
        skuInfo.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
        skuInfo.setLastPurchasePrice(skuPrice == null || skuPrice.getPurchasePrice() == null ? BigDecimal.ZERO : skuPrice.getPurchasePrice());

        return skuInfo;
    }

    public HttpEntity<byte[]> printPurchaseOrders(Short type, Long[] purchaseOrderIds) throws Exception {

        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("printDate", new Date());
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<PurchaseOrderVo> beans = new ArrayList<>();
        List<String> sheetNames = new ArrayList<>();

        for (PurchaseOrderVo purchaseOrderWrapper : purchaseOrderService.getPurchaseOrdersByIds(purchaseOrderIds)) {
            if (PurchaseOrderType.fromInt(type) == PurchaseOrderType.ACCORDING) {
                if (purchaseOrderWrapper.getStatus() != PurchaseOrderStatus.COMPLETED) {
                    purchaseOrderWrapper.setTotal(null);
                    for (PurchaseOrderItemVo itemWrapper : purchaseOrderWrapper.getPurchaseOrderItems()) {
                        itemWrapper.setPurchaseQuantity(null);
                        itemWrapper.setPurchaseBundleQuantity(null);
                        itemWrapper.setPurchasePrice(null);
                        itemWrapper.setPurchaseTotalPrice(null);
                    }
                }
            }
            sheetNames.add(String.format("采购单-%s", purchaseOrderWrapper.getId()));
            beans.add(purchaseOrderWrapper);
        }

        return ExportExcelUtils.generateExcelBytes(beans, "purchaseOrder", sheetNames, beanParams, "purchase-orders.xls", ExportExcelUtils.PURCHASE_ORDER_TEMPLATE);
    }

    public HttpEntity<byte[]> exportPurchaseOrders(PurchaseOrderRequest request, AdminUser operator) throws Exception {

        List<PurchaseOrderVo> list = purchaseOrderService.getPurchaseOrders(request, operator);

        final String fileName = "purchase-orders.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<PurchaseOrderVo>> beans = new ArrayList<>();
        beans.add(list);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("采购单列表");

        String template = PurchaseOrderType.fromInt(request.getType()) == PurchaseOrderType.ACCORDING ? ExportExcelUtils.ACCORDING_PURCHASE_ORDERS_TEMPLATE : ExportExcelUtils.STOCKUP_PURCHASE_ORDERS_TEMPLATE;
        return ExportExcelUtils.generateExcelBytes(beans, "purchaseOrders", sheetNames, beanParams, fileName, template);
    }

    public HttpEntity<byte[]> printMergedPurchaseOrdersByIds(Long[] purchaseOrderIds) throws Exception {
        return printMergedPurchaseOrders(purchaseOrderService.getPurchaseOrdersByIds(purchaseOrderIds), true);
    }

    private void calculatePurchaseOrderItems(List<PurchaseOrderVo> purchaseOrderWrappers, boolean printResult, TreeMap<Long, TreeMap<Long, PurchaseOrderItemVo>> vendorPurchaseOrderItemVosMap, Map<Long, VendorVo> simpleVendorWrapperMap) {
        for (PurchaseOrderVo purchaseOrderWrapper : purchaseOrderWrappers) {
            VendorVo simpleVendorWrapper = purchaseOrderWrapper.getVendor();
            Long vendorId = simpleVendorWrapper.getId();
            simpleVendorWrapperMap.put(vendorId, simpleVendorWrapper);

            if (vendorPurchaseOrderItemVosMap.get(vendorId) == null) {
                vendorPurchaseOrderItemVosMap.put(vendorId, new TreeMap<Long, PurchaseOrderItemVo>());
            }

            TreeMap<Long, PurchaseOrderItemVo> skuPurchaseOrderItemVosMap = vendorPurchaseOrderItemVosMap.get(vendorId);

            for (PurchaseOrderItemVo wrapper : purchaseOrderWrapper.getPurchaseOrderItems()) {
                Long skuId = wrapper.getSku().getId();
                if (skuPurchaseOrderItemVosMap.containsKey(skuId)) {
                    PurchaseOrderItemVo currentWrapper = skuPurchaseOrderItemVosMap.get(skuId);
                    currentWrapper.setNeedQuantity(currentWrapper.getNeedQuantity() + wrapper.getNeedQuantity());
                    currentWrapper.setNeedBundleQuantity(currentWrapper.getNeedBundleQuantity().add(wrapper.getNeedBundleQuantity()));

                    if (printResult) {
                        currentWrapper.setPurchaseQuantity(currentWrapper.getPurchaseQuantity() + wrapper.getPurchaseQuantity());
                        currentWrapper.setPurchasePrice(wrapper.getPurchasePrice());
                        currentWrapper.setPurchaseTotalPrice(new BigDecimal(currentWrapper.getPurchaseQuantity()).multiply(wrapper.getPurchasePrice()));
                    } else {
                        if (wrapper.getPurchaseOrder().getStatus() != PurchaseOrderStatus.COMPLETED) {
                            wrapper.setPurchaseQuantity(0);
                            wrapper.setPurchasePrice(new BigDecimal(0));
                            wrapper.setPurchaseTotalPrice(new BigDecimal(0));
                        }
                        currentWrapper.setPurchaseQuantity(currentWrapper.getPurchaseQuantity() + wrapper.getPurchaseQuantity());
                        currentWrapper.setPurchaseTotalPrice(currentWrapper.getPurchaseTotalPrice().add(wrapper.getPurchaseTotalPrice()));
                        if (currentWrapper.getPurchaseQuantity() == 0) {
                            currentWrapper.setPurchasePrice(new BigDecimal(0));
                        } else {
                            currentWrapper.setPurchasePrice(currentWrapper.getPurchaseTotalPrice().divide(new BigDecimal(currentWrapper.getPurchaseQuantity()), 6, RoundingMode.HALF_UP));
                        }
                    }
                } else {
                    if (!printResult && wrapper.getPurchaseOrder().getStatus() != PurchaseOrderStatus.COMPLETED) {
                        wrapper.setPurchaseQuantity(0);
                        wrapper.setPurchasePrice(new BigDecimal(0));
                        wrapper.setPurchaseTotalPrice(new BigDecimal(0));
                    }
                    skuPurchaseOrderItemVosMap.put(skuId, wrapper);
                }
            }
        }
    }

    public HttpEntity<byte[]> printMergedPurchaseOrders(List<PurchaseOrderVo> purchaseOrderWrappers, boolean printResult) throws Exception {

        updatePrintStatus(purchaseOrderWrappers);

        TreeMap<Long, TreeMap<Long, PurchaseOrderItemVo>> vendorPurchaseOrderItemVosMap = new TreeMap<Long, TreeMap<Long, PurchaseOrderItemVo>>();
        Map<Long, VendorVo> simpleVendorWrapperMap = new HashMap<>();

        calculatePurchaseOrderItems(purchaseOrderWrappers, printResult, vendorPurchaseOrderItemVosMap, simpleVendorWrapperMap);

        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("printDate", new Date());
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        beanParams.put("depot", purchaseOrderWrappers.get(0).getDepot().getName());

        List<PurchaseOrderGroupByVendorId> beans = new ArrayList<>();
        List<String> sheetNames = new ArrayList<>();
        long index = 0;
        for (Long vendorId : vendorPurchaseOrderItemVosMap.keySet()) {
            PurchaseOrderGroupByVendorId purchaseOrderGroupByVendorId = new PurchaseOrderGroupByVendorId();

            List<PurchaseOrderItemVo> items = new ArrayList<PurchaseOrderItemVo>(vendorPurchaseOrderItemVosMap.get(vendorId).values());

            for (PurchaseOrderItemVo wrapper : items) {
                wrapper.setId(++index);
            }
            purchaseOrderGroupByVendorId.setPurchaseOrderItems(items);

            VendorVo vendor = simpleVendorWrapperMap.get(vendorId);
            purchaseOrderGroupByVendorId.setVendor(vendor);

            sheetNames.add(String.format("采购单列表-%s", vendor.getName()));

            beans.add(purchaseOrderGroupByVendorId);
        }

        return ExportExcelUtils.generateExcelBytes(beans, "purchaseOrder", sheetNames, beanParams, "purchase-order-items.xls", ExportExcelUtils.PURCHASE_ORDER_ITEMS_TEMPLATE);
    }

    @Transactional
    private void updatePrintStatus(List<PurchaseOrderVo> purchaseOrderWrappers) {
        for (PurchaseOrderVo wrapper : purchaseOrderWrappers) {
            PurchaseOrder purchaseOrder = purchaseOrderService.getOne(wrapper.getId());
            purchaseOrder.setPrint(PurchaseOrderPrint.HASBEEN.getValue());
            purchaseOrderService.save(purchaseOrder);
        }
    }

    public HttpEntity<byte[]> printMergedPurchaseOrdersByCondition(PurchaseOrderRequest request, AdminUser adminUser) throws Exception {
        return printMergedPurchaseOrders(purchaseOrderService.getPurchaseOrders(request, adminUser), true);
    }

    public HttpEntity<byte[]> printMergedPurchaseOrdersTogetherByCondition(PurchaseOrderRequest request, AdminUser adminUser, boolean printResult) throws Exception {
        return printMergedPurchaseOrdersTogether(purchaseOrderService.getPurchaseOrders(request, adminUser), printResult);
    }

    public HttpEntity<byte[]> printMergedPurchaseOrdersTogether(List<PurchaseOrderVo> purchaseOrderWrappers, boolean printResult) throws Exception {

        String fileName = "purchase-order-items-together.xls";
        List<String> sheetNames = new ArrayList<>();
        List<PurchaseOrderGroup> beans = new ArrayList<>();

        HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("printDate", new Date());
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        if (CollectionUtils.isEmpty(purchaseOrderWrappers)) {
            sheetNames.add("empty");
            beans.add(new PurchaseOrderGroup());
            return ExportExcelUtils.generateExcelBytes(beans, "purchaseOrders", sheetNames, beanParams, fileName, ExportExcelUtils.PURCHASE_ORDER_ITEMS_TOGETHER_WITH_RESULT_TEMPLATE);
        }
        TreeMap<Long, TreeMap<Long, PurchaseOrderItemVo>> vendorPurchaseOrderItemVosMap = new TreeMap<Long, TreeMap<Long, PurchaseOrderItemVo>>();

        Map<Long, VendorVo> simpleVendorWrapperMap = new HashMap<>();

        calculatePurchaseOrderItems(purchaseOrderWrappers, printResult, vendorPurchaseOrderItemVosMap, simpleVendorWrapperMap);

        beanParams.put("depot", purchaseOrderWrappers.get(0).getDepot().getName());

        List<PurchaseOrderGroupByVendorId> bean = new ArrayList<>();

        for (Long vendorId : vendorPurchaseOrderItemVosMap.keySet()) {
            PurchaseOrderGroupByVendorId purchaseOrderGroupByVendorId = new PurchaseOrderGroupByVendorId();

            List<PurchaseOrderItemVo> items = new ArrayList<PurchaseOrderItemVo>(vendorPurchaseOrderItemVosMap.get(vendorId).values());
            purchaseOrderGroupByVendorId.setPurchaseOrderItems(items);

            BigDecimal total = new BigDecimal(0);
            for (PurchaseOrderItemVo item : items) {
                if (item.getPurchaseTotalPrice() != null) {
                    total = total.add(item.getPurchaseTotalPrice());
                }
            }
            purchaseOrderGroupByVendorId.setTotal(total);

            VendorVo vendor = simpleVendorWrapperMap.get(vendorId);
            purchaseOrderGroupByVendorId.setVendor(vendor);

            bean.add(purchaseOrderGroupByVendorId);
        }

        long index = 1;
        for (PurchaseOrderGroupByVendorId purchaseOrderGroupByVendorId : bean) {
            for (PurchaseOrderItemVo purchaseOrderItem : purchaseOrderGroupByVendorId.getPurchaseOrderItems()) {
                purchaseOrderItem.setId(index++);
            }
        }

        PurchaseOrderGroup purchaseOrderGroup = new PurchaseOrderGroup();
        purchaseOrderGroup.setPurchaseOrderGroupByVendorIdList(bean);

        BigDecimal totalAmount = new BigDecimal(0);
        for (PurchaseOrderGroupByVendorId purchaseOrderGroupByVendorId : bean) {
            totalAmount = totalAmount.add(purchaseOrderGroupByVendorId.getTotal());
        }
        purchaseOrderGroup.setTotal(totalAmount);

        beans.add(purchaseOrderGroup);

        sheetNames.add("采购单明细汇总");

        return ExportExcelUtils.generateExcelBytes(beans, "purchaseOrders", sheetNames, beanParams, fileName, ExportExcelUtils.PURCHASE_ORDER_ITEMS_TOGETHER_WITH_RESULT_TEMPLATE);
    }

    public QueryResponse<PurchaseOrderItemVo> getPurchaseOrderPreItems(Long cityId, Long vendorId) {
        List<SkuVendor> skuVendors = skuVendorService.findByCityIdAndVendorId(cityId, vendorId);

        List<PurchaseOrderItemVo> purchaseOrderItemWrappers = new ArrayList<>();
        for (SkuVendor skuVendor : skuVendors) {
            Sku sku = skuVendor.getSku();
            PurchaseOrderItemVo wrapper = new PurchaseOrderItemVo();
            wrapper.setSkuId(sku.getId());
            wrapper.setName(sku.getName());
            wrapper.setRate(sku.getRate());
            wrapper.setCapacityInBundle(sku.getCapacityInBundle());
            wrapper.setSingleUnit(sku.getSingleUnit());
            wrapper.setBundleUnit(sku.getBundleUnit());
            wrapper.setSku(new SimpleSkuWrapper(skuVendor.getSku()));
            StockTotal stockTotal = stockTotalService.findStockTotal(cityId, sku.getId());

            if (stockTotal != null) {
                wrapper.setAvgCost(stockTotal.getAvgCost());
                wrapper.setQuantity(stockTotal.getQuantity());
            } else {
                wrapper.setAvgCost(BigDecimal.ZERO);
                wrapper.setQuantity(0);
            }

            SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(cityId, sku.getId());
            wrapper.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());

            purchaseOrderItemWrappers.add(wrapper);
        }

        QueryResponse<PurchaseOrderItemVo> res = new QueryResponse<PurchaseOrderItemVo>();
        res.setSuccess(Boolean.TRUE);
        res.setContent(purchaseOrderItemWrappers);
        return res;
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDetailVo getPurchaseOrderDetailById(Long id) {

        PurchaseOrder purchaseOrder = purchaseOrderService.getOne(id);
        PurchaseOrderDetailVo vo = new PurchaseOrderDetailVo();
        vo.setPurchaseOrderId(purchaseOrder.getId());
        vo.setPurchaseOrderStatus(PurchaseOrderStatus.get(purchaseOrder.getStatus()).getName());
        vo.setPurchaseOrderType(PurchaseOrderType.fromInt(purchaseOrder.getType()).getName());
        vo.setVendorId(purchaseOrder.getVendor().getId());
        vo.setVendorName(purchaseOrder.getVendor().getName());
        vo.setCreatorName(purchaseOrder.getCreater() == null ? null : purchaseOrder.getCreater().getRealname());
        vo.setCreateTime(purchaseOrder.getCreateTime());
        vo.setAuditorName(purchaseOrder.getAuditor() == null ? null : purchaseOrder.getAuditor().getRealname());
        vo.setAuditTime(purchaseOrder.getAuditTime());
        vo.setReceiverName(purchaseOrder.getReceiver() == null ? null : purchaseOrder.getReceiver().getRealname());
        vo.setReceiveTime(purchaseOrder.getReceiveTime());
        List<PurchaseOrderItemVo> purchaseOrderItems = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            purchaseOrderItems.add(purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, item, null));
        }
        vo.setPurchaseOrderItems(purchaseOrderItems);
        List<ReturnNoteVo> returnNotes = new ArrayList<>();
        for (ReturnNote returnNote : returnNoteService.getReturnNoteByPurchaseOrderId(id)) {
            returnNotes.add(returnNoteService.returnNoteToReturnNoteVo(returnNote));
        }
        vo.setReturnNotes(returnNotes);
        return vo;
    }

    @Transactional
    public Response submitPurchaseOrderAccordingResult(AdminUser adminUser, PurchaseOrderData purchaseOrderData) {

        Long cityId = purchaseOrderData.getCityId();
        Long depotId = purchaseOrderData.getDepotId();

        List<CutOrder> cutOrders = cutOrderService.getCutOrderList(purchaseOrderData.getCutOrders());

        Response response = new Response();

        for (CutOrder cutOrder : cutOrders) {
            if (CutOrderStatus.get(cutOrder.getStatus()) != CutOrderStatus.NOTCOMMITED) {
                response.setSuccess(Boolean.FALSE);
                response.setMsg("截单数据异常，提交失败");
                return response;
            }
        }

        List<PurchaseOrderItem> allItems = purchaseOrderItemService.getItems(purchaseOrderData.getCutOrders(), null, null);

        for (PurchaseOrderItem purchaseOrderItem : allItems) {
            if (purchaseOrderItem.getPurchaseQuantity() != 0) {
                if (purchaseOrderItem.getPrice().compareTo(new BigDecimal(0)) <= 0) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMsg("存在未录入价格的sku，请检查！");
                    return response;
                }
            }
        }

        for (PurchaseOrderItem purchaseOrderItem : allItems) {
            Long vendorId = purchaseOrderItem.getPurchaseOrder().getVendor().getId();
            Long skuId = purchaseOrderItem.getSku().getId();
            String msg = String.format("请标记\"供应商-%s的skuId-%s\"后再提交。", vendorId, skuId);
            List<PurchaseOrderItemSign> signList = purchaseOrderItemSignService.findByCityIdAndDepotIdAndSkuId(cityId, depotId, skuId);

            if (CollectionUtils.isEmpty(signList)) {
                response.setSuccess(Boolean.FALSE);
                response.setMsg(msg);
                return response;
            }

            PurchaseOrderItemSign sign = signList.get(0);
            if (PurchaseOrderItemSignStatus.get(sign.getStatus()) != PurchaseOrderItemSignStatus.READY) {
                response.setSuccess(Boolean.FALSE);
                response.setMsg(msg);
                return response;
            }
        }

        for (CutOrder cutOrder : cutOrders) {
            cutOrder.setStatus(CutOrderStatus.COMMITED.getValue());
            cutOrder.setSubmitUser(adminUser);
            cutOrder.setSubmitDate(new Date());
            cutOrderService.saveCutOrder(cutOrder);
        }

        HashMap<Long, PurchaseOrder> purchaseOrderHashMap = new HashMap<>();
        for (PurchaseOrderItem purchaseOrderItem : allItems) {
            PurchaseOrder purchaseOrder = purchaseOrderItem.getPurchaseOrder();
            purchaseOrderHashMap.put(purchaseOrder.getId(), purchaseOrder);
        }

        for (PurchaseOrder purchaseOrder : purchaseOrderHashMap.values()) {
            stockInFacade.AccordingPurchaseOrderReceive(purchaseOrder, adminUser);
        }

        purchaseOrderItemSignService.deleteByCityIdAndDepotId(cityId, depotId);

        vendorOrderItemService.deleteVendorOrderItems(cutOrders);
        response.setSuccess(Boolean.TRUE);
        return response;
    }

    @Transactional
    public void changePurchaseOrderItemSign(ChangeSignRequest changeSignRequest) {
        City city = locationService.getCity(changeSignRequest.getCityId());
        Depot depot = depotService.findOne(changeSignRequest.getDepotId());
        for (Long skuId : changeSignRequest.getSkuIds()) {
            changePurchaseOrderItemSign(city, depot, skuService.getOne(skuId), PurchaseOrderItemSignStatus.get(changeSignRequest.getSign()));
        }
    }

    private void changePurchaseOrderItemSign(City city, Depot depot, Sku sku, PurchaseOrderItemSignStatus status) {
        List<PurchaseOrderItemSign> purchaseOrderItemSignList = purchaseOrderItemSignService.findByCityIdAndDepotIdAndSkuId(city.getId(), depot.getId(), sku.getId());
        if (CollectionUtils.isNotEmpty(purchaseOrderItemSignList)) {
            PurchaseOrderItemSign sign = purchaseOrderItemSignList.get(0);
            sign.setStatus(status.getValue());
            purchaseOrderItemSignService.save(sign);
        } else {
            PurchaseOrderItemSign sign = new PurchaseOrderItemSign();
            sign.setCity(city);
            sign.setDepot(depot);
            sign.setSku(sku);
            sign.setStatus(status.getValue());
            purchaseOrderItemSignService.save(sign);
        }
    }
}



