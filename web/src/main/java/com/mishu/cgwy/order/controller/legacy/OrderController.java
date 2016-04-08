package com.mishu.cgwy.order.controller.legacy;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.order.dto.*;
import com.mishu.cgwy.order.facade.LegacyOrderFacade;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.utils.LegacyOrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller("legacyOrderController")
public class OrderController {
    @Autowired
    private LegacyOrderFacade legacyOrderFacade;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/cart/add", method = RequestMethod.POST)
    @ResponseBody
    public CartAddResponse add(@RequestBody CartAddRequest shoppingRequest,
                               Principal principal) {
        OrderWrapper cart = legacyOrderFacade.addCart(shoppingRequest, principal.getName());
        CartAddResponse sr = new CartAddResponse();
        sr.setTotal(LegacyOrderUtils.countTotal(cart));
        sr.setType(LegacyOrderUtils.countType(cart));
        sr.setMoney(cart.getTotal());
        return sr;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/cart/update", method = RequestMethod.POST)
    @ResponseBody
    public CartAddResponse update(@RequestBody CartUpdateRequest shoppingUpdateRequest, Principal principal) {
        OrderWrapper cart = legacyOrderFacade.updateCart(shoppingUpdateRequest,
                principal.getName());

        CartAddResponse sr = new CartAddResponse();
        sr.setTotal(LegacyOrderUtils.countTotal(cart));
        sr.setType(LegacyOrderUtils.countType(cart));
        sr.setMoney(cart.getTotal());
        return sr;
    }

    @Deprecated
    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/cart/list", method = RequestMethod.GET)
    @ResponseBody
    public CartListResponse list(@CurrentCustomer Customer customer) {
        return legacyOrderFacade.listCart(customer);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/cart/total", method = RequestMethod.GET)
    @ResponseBody
    public CartTotalResponse total(Principal principal) {
        OrderWrapper cart = new OrderWrapper(legacyOrderFacade.getCart(principal.getName()));
        CartTotalResponse ctr = new CartTotalResponse();
        ctr.setTotal(LegacyOrderUtils.countTotal(cart));
        ctr.setType(LegacyOrderUtils.countType(cart));
        ctr.setMoney(cart.getTotal());
        return ctr;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/order/create", method = RequestMethod.POST)
    @ResponseBody
    public CreateOrderResponse create(
            @RequestBody CreateOrderRequest createOrderRequest,
            Principal principal) {
        return legacyOrderFacade.createOrder(createOrderRequest, principal.getName());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/order/list", method = RequestMethod.POST)
    @ResponseBody
    public OrderListResponse list(
            @RequestBody OrderListRequest orderListRequest, Principal principal) {
        Long restaurantId = orderListRequest.getRestaurantId();
        int status = orderListRequest.getStatus();
        return legacyOrderFacade.listOrder(restaurantId, status, principal.getName());
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/order/listdetail/{orderNumber}", method = RequestMethod.GET)
    @ResponseBody
    public OrderDetailResponse detail(@PathVariable String orderNumber) {
        return legacyOrderFacade.getOrderDetail(orderNumber);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/order/cancel/{orderNumber}", method = RequestMethod.GET)
    @ResponseBody
    public RestError cancel(@PathVariable String orderNumber) {
        legacyOrderFacade.cancel(orderNumber);
        return new RestError();
    }

    @Deprecated
    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/history", method = RequestMethod.POST)
    @ResponseBody
    public OrderHistoryResponse getOrderHistoryList(@RequestBody OrderHistoryRequest request, @CurrentCustomer Customer customer) {
        OrderHistoryResponse response = new OrderHistoryResponse();
        List<OrderHistoryItem> list = legacyOrderFacade.buildSortedOrderHistory(customer, request.getRestaurantId(),
                request.getSort(), request.getOrder(), request.getPage(), request.getRows());
        response.setHistoryList(list);
        response.setTotal(list.size());
        return response;
    }
    @Secured("ROLE_USER")
    @RequestMapping(value="/api/order/{id}/feedback",method = RequestMethod.POST)
    public void customerEvaluate(@PathVariable("id") Long orderId,@RequestBody CustomerEvaluateRequest request, @CurrentCustomer Customer customer){
    	legacyOrderFacade.addCustomerEvaluate(orderId,request,customer);
    }
}

