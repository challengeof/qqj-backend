package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.constants.CancelOrderReasonRequest;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.order.constants.SellCancelReason;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.wrapper.OrderInfoWrapper;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.product.facade.ProductExcelFacade;
import com.mishu.cgwy.score.Wrapper.ScoreLogWrapper;
import com.mishu.cgwy.score.facade.ScoreFacade;
import com.mishu.cgwy.stock.facade.StockOutFacade;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:47 PM
 */
@Controller
public class OrderController {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductExcelFacade productExcelFacade;
    @Autowired
    private StockOutFacade stockOutFacade;
    @Autowired
    private ScoreFacade scoreFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setLenient(false);*/
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @RequestMapping(value = "/api/order", method = RequestMethod.GET)
    @ResponseBody
    public OrderQueryResponse listOrders(OrderQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return orderFacade.findOrders(request, operator);
    }

    /**
     * 订单备注
     * @param id
     * @param request
     * @param operator
     */
    @RequestMapping(value = "/api/order/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateOrder(@PathVariable("id") Long id, @RequestBody OrderUpdateRequest request, @CurrentAdminUser AdminUser operator) {
        orderFacade.updateOrder(id, request, operator);
    }

    @RequestMapping(value = "/api/order/{id}/deliver", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper deliverOrder(@PathVariable("id") Long id, @CurrentAdminUser AdminUser operator) {
        orderFacade.deliverOrder(id, operator);
        return orderFacade.adminGetOrderById(id);
    }

    @RequestMapping(value = "/api/order/{id}/fulfillment", method = RequestMethod.PUT)
    @ResponseBody
    public void updateFulfillment(@PathVariable("id") Long id, @RequestBody FulfillmentRequest request, @CurrentAdminUser AdminUser operator) {
        orderFacade.updateFulfillment(id, request, operator);
    }

    @RequestMapping(value = "/api/order/item", method = RequestMethod.GET)
    @ResponseBody
    public OrderItemQueryResponse listOrderItems(OrderItemQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return orderFacade.findOrderItems(request, operator);
    }

    @RequestMapping(value = "/api/order/item/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> listOrderItemsExport(OrderItemQueryRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        return orderFacade.itemExport(request, adminUser);
    }

    @RequestMapping(value = "/api/order/status", method = RequestMethod.GET)
    @ResponseBody
    public List<OrderStatus> listOrderStatus() {
        final OrderStatus[] values = OrderStatus.values();
        final List<OrderStatus> list = new ArrayList<>(Arrays.asList(values));
        list.remove(OrderStatus.UNCOMMITTED);
        return list;
    }

    @RequestMapping(value = "/api/order/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OrderWrapper adminGetOrderById(@PathVariable Long id) {
        return orderFacade.adminGetOrderById(id);
    }

    @RequestMapping(value = "/api/order/info/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OrderInfoWrapper getOrderInfoById(@PathVariable Long id) {
        return orderFacade.getOrderInfoById(id);
    }

    @RequestMapping(value = "/api/order/{id}/cancel", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper cancelOrder(@PathVariable("id") Long id, @CurrentAdminUser AdminUser operator) {
        orderFacade.cancelOrder(id, operator);
        return orderFacade.adminGetOrderById(id);
    }

    @RequestMapping(value = "/api/order/{id}/complete", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper completeOrder(@PathVariable("id") Long id, @CurrentAdminUser AdminUser operator) {
        orderFacade.completeOrder(id, operator);
        return orderFacade.adminGetOrderById(id);
    }

    @RequestMapping(value = "/api/order/skus", method = RequestMethod.GET)
    @ResponseBody
    public OrderSearchSkusResponse getSkus(OrderGroupQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return orderFacade.getOrderSkus(request, operator);
    }

    @RequestMapping(value = "/api/sku/sales", method = RequestMethod.GET)
    @ResponseBody
    public SkuSaleResponse getSkuSales(SkuSalesRequest request, @CurrentAdminUser AdminUser operator) {
        return orderFacade.findSkuSales(request, operator);
    }

    @RequestMapping(value = "/api/sku-sale-detail/excelExport", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> excelExport(SkuSalesRequest request, @CurrentAdminUser AdminUser adminUser) throws IOException {
        request.setPageSize(Integer.MAX_VALUE);
        File file = productExcelFacade.skuSaleExport(request, adminUser, "excel", "workbook.xls");
        return ExportExcelUtils.getHttpEntityXlsx(file.getPath());
    }

    @RequestMapping(value = "/api/order/evaluate", method = RequestMethod.GET)
    @ResponseBody
    public OrderEvaluateResponse getEvaluate(OrderEvaluateSearchRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        return orderFacade.getEvaluates(request, adminUser);
    }

    @RequestMapping(value = "/api/order/evaluate/score/send", method = RequestMethod.GET)
    @ResponseBody
    public ScoreLogWrapper evaluateBakScore(@RequestParam("orderId") Long orderId, @CurrentAdminUser AdminUser adminUser) throws Exception {
        //根据订单id返积分
        return scoreFacade.evaluateBakScore(orderId, adminUser);
    }


    @RequestMapping(value = "/api/order-evaluate/excelExport", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> evaluateExcelExport(OrderEvaluateSearchRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        request.setPageSize(Integer.MAX_VALUE);
        File file = orderFacade.evaluateOrderExcelExport(request, adminUser, "excel", "evaluateExcel.xls");
        return ExportExcelUtils.getHttpEntityXlsx(file.getPath());
    }

    @RequestMapping(value = "/api/order/excelExport", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> excelExport(OrderQueryRequest request, @CurrentAdminUser AdminUser operator) throws IOException {
        File file = orderFacade.ordersExcelExport(request, operator, "excel", "workbook.xls");
        return ExportExcelUtils.getHttpEntityXlsx(file.getPath());
    }

    @RequestMapping(value = "/api/order/reason", method = RequestMethod.GET)
    @ResponseBody
    public List<SellCancelReason> getReasonStatus() {
        return new ArrayList<>(Arrays.asList(SellCancelReason.values()));
    }

    @RequestMapping(value = "/api/order/cancel/reason", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper cancelOrderReason(@RequestBody CancelOrderReasonRequest reasonRequest, @CurrentAdminUser AdminUser operator) {
        orderFacade.cancelOrder(reasonRequest, operator);
        return orderFacade.adminGetOrderById(reasonRequest.getOrderId());
    }

    @RequestMapping(value = "/api/order/stop", method = RequestMethod.GET)
    @ResponseBody
    public void stopOrder(OrderQueryRequest request, @CurrentAdminUser AdminUser operator) {
        stockOutFacade.createStockOut(request, operator);
    }

    @RequestMapping(value = "/api/order/create", method = RequestMethod.POST)
    @ResponseBody
    public List<OrderWrapper> createOrder(@CurrentAdminUser AdminUser operator, @RequestBody OrderCreateRequest request) {
        return orderFacade.createOrder(operator, request);
    }

    @RequestMapping(value = "/api/order/orderType/get", method = RequestMethod.GET)
    @ResponseBody
    public OrderType[] getOrderTypes() {
        return OrderType.values();
    }

}
