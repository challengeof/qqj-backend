package com.mishu.cgwy.order.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.operating.skipe.service.SpikeCacheService;
import com.mishu.cgwy.order.controller.legacy.MyOrderResponse;
import com.mishu.cgwy.order.exception.SpikeOutOfStockException;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.wrapper.*;
import com.mishu.cgwy.product.wrapper.CartSimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.stock.facade.SellCancelFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: xudong
 * Date: 5/20/15
 * Time: 6:12 PM
 */
@Controller
public class WebOrderController {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private CouponService couponService;

    @Autowired
    private SellCancelFacade sellCancelFacade;

    @Autowired
    private SpikeCacheService spikeCacheService;

    private Logger logger = LoggerFactory.getLogger(WebOrderController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/legacy/my-order", method = RequestMethod.GET)
    @ResponseBody
    public MyOrderResponse legacyMyOrderToday(@CurrentCustomer Customer customer) {
        if (customer == null) {
            return new MyOrderResponse();
        } else {
            MyOrderResponse result = new MyOrderResponse();
            result.setOrders(orderFacade.myOrderToday(customer));

            return result;
        }
    }

    @RequestMapping(value = "/api/v2/today-order", method = RequestMethod.GET)
    @ResponseBody
    public List<OrderWrapper> myOrderToday(@CurrentCustomer Customer customer) {
        return orderFacade.myOrderToday(customer);
    }


    @RequestMapping(value = "/api/v2/order", method = RequestMethod.POST)
    @ResponseBody
    public List<OrderWrapper> createOrder(@CurrentCustomer Customer customer, @RequestBody List<CartRequest> cartRequest) {
        return orderFacade.createOrder(customer, cartRequest, null, "");
    }

    @RequestMapping(value = "/api/v2/order-coupon", method = RequestMethod.POST)
    @ResponseBody
    public List<OrderWrapper> createOrder(@CurrentCustomer Customer customer, @RequestBody CartAndCouponRequest cartAndCouponRequest) {

        //创建订单前 若存在秒杀商品  进行库存预判断
        orderFacade.spikeItemNumCheck(customer, cartAndCouponRequest.getCartRequestList().toArray(new CartRequest[]{}));
        List<OrderWrapper> result = orderFacade.createOrderCoupon(customer, cartAndCouponRequest);
        orderFacade.incrSpikeLimitByCustomer(customer, cartAndCouponRequest.getCartRequestList());
        return result;

    }

    @RequestMapping(value = "/api/v2/order/preview", method = RequestMethod.POST)
    @ResponseBody
    public List<OrderWrapper> previewOrder(@CurrentCustomer Customer customer, @RequestBody List<CartRequest>
            cartRequest) {
        return orderFacade.previewOrderWrapper(customer, cartRequest);
    }

    @RequestMapping(value = "/api/v2/order/available-coupon", method = RequestMethod.POST)
    @ResponseBody
    public List<CustomerCouponWrapper> getAvailableCoupon(@CurrentCustomer Customer customer, @RequestBody List<CartRequest> cartRequest) {
        List<CustomerCouponWrapper> customerCoupons = new ArrayList<>();
        for (CustomerCoupon customerCoupon : orderFacade.findAvailableCoupons(customer, cartRequest)) {
            customerCoupons.add(new CustomerCouponWrapper(customerCoupon));
        }
        return customerCoupons;
    }

    @RequestMapping(value = "/api/v2/order/stock", method = RequestMethod.PUT)
    @ResponseBody
    public List<CartSimpleSkuWrapper> checkStockOut(@CurrentCustomer Customer customer, @RequestBody List<CartRequest> cartRequest) {
        List<CartSimpleSkuWrapper> stockOutWrappers = orderFacade.checkStockOut(customer, cartRequest);

        return stockOutWrappers;
    }

    @RequestMapping(value = "/api/v2/order/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public OrderWrapper cancelOrder(@PathVariable("id") Long orderId, @RequestParam(value = "deviceId", required = false) String deviceId, @CurrentCustomer Customer customer) {
        logger.warn("app2.0取消订单 " + orderId);
        sellCancelFacade.createSellCancel(orderId, deviceId,customer);
        return orderFacade.webGetOrderById(orderId);
    }

    @RequestMapping(value = "/api/v2/order", method = RequestMethod.GET)
    @ResponseBody
    public OrderQueryResponse findOrders(OrderQueryRequest request, @CurrentCustomer Customer customer) {
        return orderFacade.findOrdersByCustomer(request, customer);
    }

    @RequestMapping(value = "/api/v2/order/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OrderWrapper findOrder(@CurrentCustomer Customer customer, @PathVariable("id") Long orderId) {
        return orderFacade.webGetOrderById(orderId);
    }


    @RequestMapping(value = "/api/v2/cart", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper addCart(@CurrentCustomer Customer customer, @RequestBody List<CartRequest> cartRequest) {
        return orderFacade.addSkuToCart(customer.getId(), cartRequest, true);
    }

    @RequestMapping(value = "/api/v2/cart", method = RequestMethod.PUT)
    @ResponseBody
    public OrderWrapper syncSkuQuantity(@CurrentCustomer Customer customer, @RequestBody List<CartRequest>
            cartRequest) {
        return orderFacade.addSkuToCart(customer.getId(), cartRequest, false);
    }


    @RequestMapping(value = "/api/v2/cart", method = RequestMethod.DELETE)
    @ResponseBody
    public OrderWrapper cancelSku(@CurrentCustomer Customer customer, @RequestParam("itemIds") List<Long> itemIds) {
        return orderFacade.removeSkuFromCart(customer, itemIds);
    }


    @RequestMapping(value = "/api/v2/cart", method = RequestMethod.GET)
    @ResponseBody
    public OrderWrapper getCart(@CurrentCustomer Customer customer) {
        return orderFacade.getCart(customer);
    }

    @RequestMapping(value = "/api/v2/coupon", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerCouponWrapper> findCustomerCoupons(@CurrentCustomer Customer customer) {
        List<CustomerCouponWrapper> customerCoupons = new ArrayList<>();
        boolean flag = false;
        for (CustomerCoupon customerCoupon : couponService.findByCustomer(customer)) {

            if (customerCoupon.getEnd().before(new Date()) && CouponStatus.UNUSED.getValue().equals(customerCoupon.getStatus())) {
                customerCoupon.setStatus(CouponStatus.EXPIRED.getValue());
                couponService.saveCustomerCoupon(customerCoupon);
                flag = true;
            }
            customerCoupons.add(new CustomerCouponWrapper(customerCoupon));
        }
        if (flag) {

            Collections.sort(customerCoupons, new Comparator<CustomerCouponWrapper>() {
                @Override
                public int compare(CustomerCouponWrapper o1, CustomerCouponWrapper o2) {
                    return o1.getStatus().compareTo(o2.getStatus());
                }
            });
        }
        return customerCoupons;
    }

    @RequestMapping(value = "/api/v2/{id}/evaluate", method = RequestMethod.POST)
    @ResponseBody
    public EvaluateWrapper addEvaluate(@CurrentCustomer Customer customer, @PathVariable("id") Long orderId, @RequestBody EvaluateWrapper evaluate) {

        return orderFacade.addEvaluate(customer, orderId, evaluate);

    }

    @RequestMapping(value = "/api/v2/order/maxOrder", method = RequestMethod.GET)
    @ResponseBody
    public List<PcOrderResponse> getMaxOrder() {

        return orderFacade.findMaxOrders();
    }

    @RequestMapping(value = "/api/v2/order/evaluate/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public EvaluateWrapper getEvaluate(@PathVariable("orderId") Long orderId) {

        return orderFacade.getEvaluateByOrder(orderId);
    }
}

