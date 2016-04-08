package com.mishu.cgwy.background.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.dto.OrderDetailResp;
import com.mishu.cgwy.order.dto.OrderListRequest;
import com.mishu.cgwy.order.dto.OrderListResponse;
import com.mishu.cgwy.order.dto.RiskOrderResponse;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.facade.OrderGroupFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bowen on 15-5-4.
 */
@Controller
public class BackgroundController {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderGroupFacade orderGroupFacade;

    private Logger logger = LoggerFactory.getLogger(BackgroundController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/out/search/service/deliver",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse getAdminOrderListById(@CurrentAdminUser AdminUser operator){

        return orderFacade.getAdminOrderListById(operator);
    }

    @RequestMapping(value = "/api/out/search/time/order",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse getTodayNewOrders(@CurrentAdminUser AdminUser operator) {

        return orderFacade.getTodayNewOrders(operator);
    }

    @RequestMapping(value = "/api/order/complete",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse successOrder(@CurrentAdminUser AdminUser operator,@RequestBody OrderListRequest orderListRequest) {

        return orderFacade.successOrder(operator, orderListRequest);
    }

    @RequestMapping(value = "/api/out/create/return",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse problemOrder(@RequestBody OrderListRequest orderListRequest){

        return new OrderListResponse();
    }

    @RequestMapping(value = "/api/out/create/return/detail",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse handleOrder(@RequestBody OrderListRequest orderListRequest, @CurrentAdminUser AdminUser operator) {
//
//        return orderFacade.handleOrder(orderListRequest, operator);
        OrderListResponse response = new OrderListResponse();
        response.setErrno(-1);
        response.setErrmsg("客户端app暂不支持退货，请在后台系统退货");

        return response;
    }

    @Deprecated
    @RequestMapping(value = "/api/out/search/order/detail/{orderId}",method = RequestMethod.GET)
    @ResponseBody
    public OrderDetailResp getOrderDetailById(@PathVariable Long orderId){

        return orderFacade.getOrderDetailById(orderId);
    }

    @RequestMapping(value = "/api/out/accept/{orderNumber}", method = RequestMethod.GET)
    @ResponseBody
    public OrderListResponse noExceptionOrder(@PathVariable Long orderId, @CurrentAdminUser AdminUser operator) {

        return orderFacade.noExceptionOrder(orderId, operator);
    }

    @RequestMapping(value = "/api/out/search/risk", method = RequestMethod.POST)
    @ResponseBody
    public RiskOrderResponse getOrderRisk() {

        return new RiskOrderResponse();
    }

    @RequestMapping(value = "/api/terminal/check/update", method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse versionUpdate() {

        return new OrderListResponse();
    }

    @RequestMapping(value = "/api/out/search/deliver",method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse getDriverOrderListById(@CurrentAdminUser AdminUser operator){

        return orderFacade.getDriverOrderListById(operator);
    }
}
