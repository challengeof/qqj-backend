package com.mishu.cgwy.purchase.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.purchase.domain.PurchaseOrderPrint;
import com.mishu.cgwy.purchase.domain.PurchaseOrderStatus;
import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.purchase.enumeration.PurchaseOrderItemSignStatus;
import com.mishu.cgwy.purchase.facade.CutOrderFacade;
import com.mishu.cgwy.purchase.facade.PurchaseOrderFacade;
import com.mishu.cgwy.purchase.service.PurchaseOrderItemService;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.purchase.vo.CutOrderVo;
import com.mishu.cgwy.purchase.vo.PurchaseOrderDetailVo;
import com.mishu.cgwy.purchase.vo.PurchaseOrderItemVo;
import com.mishu.cgwy.purchase.vo.PurchaseOrderVo;
import com.mishu.cgwy.purchase.wrapper.*;
import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.SubmitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
@Controller
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderFacade purchaseOrderFacade;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Autowired
    private CutOrderFacade cutOrderFacade;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @RequestMapping(value = "/api/purchase/order/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<PurchaseOrderVo> list(PurchaseOrderRequest request, @CurrentAdminUser AdminUser opertor) {
        return purchaseOrderService.getPurchaseOrderList(request, opertor);
    }

    @RequestMapping(value = "/api/purchase/order/list/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportPurchaseOrders(PurchaseOrderRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return purchaseOrderFacade.exportPurchaseOrders(request, operator);
    }

    @RequestMapping(value = "/api/purchase/order/items", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseAccordingResultResponse<PurchaseOrderItemVo> items(PurchaseOrderRequest request) throws Exception {
        return purchaseOrderItemService.getPurchaseOrderItems(request);
    }

    @RequestMapping(value = "/api/purchase/order/statuses", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderStatus[] getPurchaseOrderStatuses() {
        return PurchaseOrderStatus.values();
    }

    @RequestMapping(value = "/api/purchase/order/types", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderType[] getPurchaseOrderTypes() {
        return PurchaseOrderType.values();
    }

    @RequestMapping(value = "/api/purchase/order/printStatus", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderPrint[] getPurchaseOrderPrintStatus() {
        return PurchaseOrderPrint.values();
    }

    @RequestMapping(value = "/api/purchase/order/sku", method = RequestMethod.GET)
    @ResponseBody
    public SkuInfo getSkuInfo(@RequestParam("cityId") Long cityId, @RequestParam("skuId") Long skuId, @RequestParam(value = "status", required = false) Integer status) {
        return purchaseOrderFacade.getSkuInfo(cityId, skuId, status);
    }

    @RequestMapping(value = "/api/purchase/order/preItems", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<PurchaseOrderItemVo> getPurchaseOrderPreItems(@RequestParam("cityId") Long cityId, @RequestParam("vendorId") Long vendorId) {
        return purchaseOrderFacade.getPurchaseOrderPreItems(cityId, vendorId);
    }

    @RequestMapping(value = "/api/purchase/order/add", method = RequestMethod.POST)
    @ResponseBody
    public void savePurchaseOrder(@CurrentAdminUser AdminUser adminUser, @RequestBody PurchaseOrderData purchaseOrderData) {
        purchaseOrderData.setType(PurchaseOrderType.STOCKUP.getValue());
        purchaseOrderService.savePurchaseOrder(adminUser, purchaseOrderData);
    }

    @RequestMapping(value = "/api/purchase/order/accordingResult", method = RequestMethod.POST)
    @ResponseBody
    public void savePurchaseOrderAccordingResult(@RequestBody PurchaseOrderData purchaseOrderData) {
        purchaseOrderService.savePurchaseOrderAccordingResult(purchaseOrderData);
    }

    @RequestMapping(value = "/api/purchase/order/submitAccordingResult", method = RequestMethod.POST)
    @ResponseBody
    public Response submitPurchaseOrderAccordingResult(@CurrentAdminUser AdminUser adminUser, @RequestBody PurchaseOrderData purchaseOrderData) {
        return purchaseOrderFacade.submitPurchaseOrderAccordingResult(adminUser, purchaseOrderData);
    }

    @RequestMapping(value = "/api/purchase/order/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderVo getPurchaseOrder(@PathVariable("id") Long id) {
        return purchaseOrderService.getPurchaseOrder(id);
    }

    @RequestMapping(value = "/api/purchase/order/info/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderDetailVo getPurchaseOrderDetailById(@PathVariable("id") Long id) {
        return purchaseOrderFacade.getPurchaseOrderDetailById(id);
    }

    @RequestMapping(value = "/api/purchase/order/submit/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderStatus submit(@PathVariable("id") Long id) {
        return PurchaseOrderStatus.get(purchaseOrderService.submit(id).getStatus());
    }

    @RequestMapping(value = "/api/purchase/order/cancel/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SubmitResponse<PurchaseOrderVo> cancel(@PathVariable("id")Long id,  @CurrentAdminUser AdminUser operator) {
        return purchaseOrderService.cancel(id, operator);
    }

    @RequestMapping(value = "/api/purchase/order/audit", method = RequestMethod.POST)
    @ResponseBody
    public void audit(@CurrentAdminUser AdminUser adminUser, @RequestBody PurchaseOrderData purchaseOrderData) {
        purchaseOrderService.audit(adminUser, purchaseOrderData);
    }

    @RequestMapping(value = "/api/purchase/order/cut-order-list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<CutOrderVo> list(CutOrderListRequest request) {
        return cutOrderFacade.getCutOrderList(request);
    }

    @RequestMapping(value = "/api/purchase/order/createAccordingResult", method = RequestMethod.POST)
    @ResponseBody
    public void createAccordingResult(@RequestBody List<Long> cutOrders) {
        cutOrderFacade.createAccordingResult(cutOrders);
    }

    @RequestMapping(value = "/api/purchase/order/printPurchaseOrders", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printPurchaseOrders(@RequestParam("type") Short type, @RequestParam("purchaseOrderIds") Long[] purchaseOrderIds) throws Exception {
        return purchaseOrderFacade.printPurchaseOrders(type, purchaseOrderIds);
    }

    @RequestMapping(value = "/api/purchase/order/printMergedPurchaseOrdersByIds", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printMergedPurchaseOrdersByIds(@RequestParam("purchaseOrderIds") Long[] purchaseOrderIds) throws Exception {
        return purchaseOrderFacade.printMergedPurchaseOrdersByIds(purchaseOrderIds);
    }

    @RequestMapping(value = "/api/purchase/order/printMergedPurchaseOrdersByCondition", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printMergedPurchaseOrdersByCondition(PurchaseOrderRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return purchaseOrderFacade.printMergedPurchaseOrdersByCondition(request, operator);
    }

    @RequestMapping(value = "/api/purchase/order/printMergedPurchaseOrdersTogetherByCondition", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printMergedPurchaseOrdersTogetherByCondition(PurchaseOrderRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return purchaseOrderFacade.printMergedPurchaseOrdersTogetherByCondition(request, operator, false);
    }

    @RequestMapping(value = "/api/purchase/order/printMergedPurchaseOrdersResultTogetherByCondition", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printMergedPurchaseOrdersResultTogetherByCondition(PurchaseOrderRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return purchaseOrderFacade.printMergedPurchaseOrdersTogetherByCondition(request, operator, true);
    }

    @RequestMapping(value = "/api/purchase/order/changePurchaseOrderItemSign", method = RequestMethod.POST)
    @ResponseBody
    public void changePurchaseOrderItemSign(@RequestBody ChangeSignRequest changeSignRequest) {
        purchaseOrderFacade.changePurchaseOrderItemSign(changeSignRequest);
    }

    @RequestMapping(value = "/api/purchase/order/item/signList", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrderItemSignStatus[] getPurchaseOrderItemSigns() {
        return PurchaseOrderItemSignStatus.values();
    }
}
