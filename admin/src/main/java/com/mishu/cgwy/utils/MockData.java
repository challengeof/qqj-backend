package com.mishu.cgwy.utils;

import com.mishu.cgwy.common.wrapper.SimpleRegionWrapper;
import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.OrderItemQueryRequest;
import com.mishu.cgwy.order.controller.OrderItemQueryResponse;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.controller.OrderQueryResponse;
import com.mishu.cgwy.order.wrapper.OrderItemWrapper;
import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.controller.RestaurantQueryResponse;
import com.mishu.cgwy.profile.wrapper.AddressWrapper;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/3/15
 * Time: 10:59 AM
 */
public class MockData {
    public static RestaurantQueryResponse newRestaurantQueryResponse(RestaurantQueryRequest request) {
        if (request == null) {
            request = new RestaurantQueryRequest();
        }

        RestaurantQueryResponse result = new RestaurantQueryResponse();
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(1024);

        List<RestaurantWrapper> restaurantWrappers = new ArrayList<RestaurantWrapper>();

        for (long i = 0; i < request.getPageSize(); i++) {
            RestaurantWrapper r = newRestaurantWrapper(i);

            restaurantWrappers.add(r);

        }

        result.setRestaurants(restaurantWrappers);

        return result;
    }

    public static RestaurantWrapper newRestaurantWrapper(Long id) {
        RestaurantWrapper r = new RestaurantWrapper();
        r.setId(id);
        r.setName("restaurant name " + id);
        r.setCustomer(newCustomerWrapper(id));
        r.setAddress(newAddressWrapper());
        r.setStatus(RestaurantStatus.ACTIVE);
        r.setTelephone("11111111111");
        return r;
    }

    public static SimpleRestaurantWrapper newSimpleRestaurantWrapper(Long id) {
        SimpleRestaurantWrapper r = new SimpleRestaurantWrapper();
        r.setId(id);
        r.setName("restaurant name " + id);
        r.setAddress(newAddressWrapper());
        r.setStatus(RestaurantStatus.ACTIVE.getValue());
        r.setTelephone("11111111111");
        return r;
    }

    public static OrderQueryResponse newOrderQueryResponse(OrderQueryRequest request) {
        if (request == null) {
            request = new OrderQueryRequest();
        }

        OrderQueryResponse result = new OrderQueryResponse();
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(1024);

        List<SimpleOrderWrapper> simpleOrderWrappers = new ArrayList<SimpleOrderWrapper>();

        for (long i = 0; i < request.getPageSize(); i++) {
            SimpleOrderWrapper r = newOrderWrapper(i);

            simpleOrderWrappers.add(r);

        }

        result.setOrders(simpleOrderWrappers);

        return result;
    }

    public static OrderItemQueryResponse newOrderQueryResponse(OrderItemQueryRequest request) {
        if (request == null) {
            request = new OrderItemQueryRequest();
        }

        OrderItemQueryResponse result = new OrderItemQueryResponse();
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(1024);

        List<OrderItemWrapper> orderItemWrappers = new ArrayList<OrderItemWrapper>();

        for (long i = 0; i < request.getPageSize(); i++) {
            OrderItemWrapper r = newOrderItemWrapper(i);

            orderItemWrappers.add(r);

        }

        result.setOrderItems(orderItemWrappers);

        return result;
    }


    public static SimpleOrderWrapper newOrderWrapper(Long id) {
        SimpleOrderWrapper r = new SimpleOrderWrapper();
        r.setId(id);
        r.setStatus(OrderStatus.COMMITTED);
        r.setMemo("my memo " + id);
        r.setOrderNumber("my order number " + id);
        r.setRestaurant(newSimpleRestaurantWrapper(id));
        r.setTotal(BigDecimal.TEN);
        r.setShipping(BigDecimal.ZERO);
        r.setSubTotal(BigDecimal.TEN);
        r.setSubmitDate(new Date());

        return r;
    }

    public static OrderItemWrapper newOrderItemWrapper(Long id) {
        OrderItemWrapper r = new OrderItemWrapper();
        r.setId(id);
        r.setOrderId(id);
        r.setPrice(BigDecimal.ONE);
        r.setTotalPrice(BigDecimal.ONE);
        r.setQuantity(1);
        r.setSku(newSkuWrapper(id));
        return r;
    }

    public static SimpleSkuWrapper newSkuWrapper(Long id) {
        SimpleSkuWrapper skuWrapper = new SimpleSkuWrapper();
        skuWrapper.setId(id);
        skuWrapper.setName("sku name " + id);
        skuWrapper.setStatus(SkuStatus.ACTIVE);
        skuWrapper.setSalePrice(BigDecimal.TEN);
        skuWrapper.setBundle(false);
        skuWrapper.setCapacityInBundle(1);
        skuWrapper.setMarketPrice(BigDecimal.TEN);
        final BrandWrapper brand = new BrandWrapper();
        brand.setId(id);
        brand.setBrandName("brand " + id);

        skuWrapper.setBrand(brand);
        return skuWrapper;
    }

    private static CategoryWrapper newCategoryWrapper(Long id) {
        CategoryWrapper categoryWrapper = new CategoryWrapper();
        categoryWrapper.setId(id);
        categoryWrapper.setName("category name " + id);
        categoryWrapper.setHierarchyName("category hierarchy name " + id);
        return categoryWrapper;
    }

    public static CustomerWrapper newCustomerWrapper(Long id) {
        CustomerWrapper customerWrapper = new CustomerWrapper();
        customerWrapper.setId(id);
        customerWrapper.setUsername("customer id " + id);
        customerWrapper.setUserNumber("customer number " + id);
        return customerWrapper;
    }

    public static AddressWrapper newAddressWrapper() {
        AddressWrapper addressWrapper = new AddressWrapper();
        addressWrapper.setAddress("address xyz");
        addressWrapper.setWgs84Point(null);

        return addressWrapper;
    }

    public static ZoneWrapper newZoneWrapper(Long id) {
        ZoneWrapper zoneWrapper = new ZoneWrapper();
        zoneWrapper.setId(id);
        zoneWrapper.setName("zone name " + id);
        zoneWrapper.setDisplayName("zone display name " + id);
        return zoneWrapper;
    }

    public static SimpleRegionWrapper newRegionWrapper(Long id) {
        SimpleRegionWrapper regionWrapper = new SimpleRegionWrapper();
        regionWrapper.setId(id);
        regionWrapper.setName("region name " + id);
        regionWrapper.setDisplayName("region display name " + id);
        return regionWrapper;
    }


}
