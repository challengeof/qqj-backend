package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.facade.OrderGroupFacade;
import com.mishu.cgwy.order.wrapper.OrderGroupWrapper;
import com.mishu.cgwy.order.wrapper.SimpleOrderGroupWrapper;
import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/15/15
 * Time: 5:03 PM
 */
@Controller
public class OrderGroupController {
    @Autowired
    private OrderGroupFacade orderGroupFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/api/order-group", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleOrderGroupWrapper> findOrderGroups(
            OrderGroupQueryRequest request,@CurrentAdminUser AdminUser adminUser) {
        return orderGroupFacade.findOrderGroups(request, adminUser);
    }

    @RequestMapping(value = "/api/order-group", method = RequestMethod.POST)
    @ResponseBody
    public SimpleOrderGroupWrapper createOrderGroup(@RequestBody OrderGroupRequest request, @CurrentAdminUser AdminUser
            operator) {
        return orderGroupFacade.groupOrders(request, operator);
    }

    @RequestMapping(value = "/api/order-group/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public OrderGroupWrapper updateOrderGroup(@PathVariable("id") Long id,
                                                    @RequestBody OrderGroupRequest request,
                                                    @CurrentAdminUser AdminUser operator) {
        return orderGroupFacade.updateOrderGroup(id, request, operator);
    }

    @RequestMapping(value = "/api/order-group/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OrderGroupWrapper getOrderGroup(@PathVariable("id") Long id,
                                                 @CurrentAdminUser AdminUser operator) {
        return orderGroupFacade.getOrderGroup(id);
    }

    @RequestMapping(value = "/api/ungrouped-order", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleOrderWrapper> findUnGroupOrders(StockOutRequest request,
                                                      @CurrentAdminUser AdminUser operator) {
        return orderGroupFacade.findUnGroupedOrders(request, operator);
    }

    @RequestMapping(value = "/api/ungrouped-order/size", method = RequestMethod.GET)
    @ResponseBody
    public Long unGroupOrdersSize(StockOutRequest request,
                                 @CurrentAdminUser AdminUser operator) {
        return orderGroupFacade.findUnGroupedOrders(request,operator).getTotal();
    }

}
