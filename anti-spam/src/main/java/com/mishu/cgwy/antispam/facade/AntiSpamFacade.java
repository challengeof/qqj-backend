package com.mishu.cgwy.antispam.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.wrapper.AdminUserWrapper;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 8/26/15
 * Time: 4:10 PM
 */
@Service
public class AntiSpamFacade {
    @Autowired
    private OrderService orderService;


    @Transactional(readOnly = true)
    public Map<AdminUserWrapper, Map<RestaurantWrapper, List<OrderWrapper>>> findSuspectFakeRestaurant(Date date) {
        OrderQueryRequest request = new OrderQueryRequest();

        Date expected = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addDays(expected, 1);
        Date start = expected;

        request.setStart(start);
        request.setEnd(end);
        request.setPageSize(Integer.MAX_VALUE);
        final Page<Order> orders = orderService.findOrders(request, null);

        Map<AdminUser, Map<Restaurant, List<Order>>> adminUserRestaurantOrderMap = new HashMap<>();
        for (Order order : orders) {
            final AdminUser adminUser = order.getAdminUser();
            final Restaurant restaurant = order.getRestaurant();

            if (!adminUserRestaurantOrderMap.containsKey(adminUser)) {
                adminUserRestaurantOrderMap.put(adminUser, new HashMap<Restaurant, List<Order>>());
            }

            final Map<Restaurant, List<Order>> restaurantOrderMap = adminUserRestaurantOrderMap.get(adminUser);
            if (!restaurantOrderMap.containsKey(restaurant)) {
                restaurantOrderMap.put(restaurant, new ArrayList<Order>());
            }

            restaurantOrderMap.get(restaurant).add(order);
        }

        Map<AdminUserWrapper, Map<RestaurantWrapper, List<OrderWrapper>>> suspect = new LinkedHashMap<>();
        for (AdminUser adminUser : adminUserRestaurantOrderMap.keySet()) {
            final Map<Restaurant, List<Order>> restaurantListMap = adminUserRestaurantOrderMap.get(adminUser);

            final ArrayList<Restaurant> restaurants = new ArrayList<>(restaurantListMap.keySet());
            for (int i = 0; i < restaurants.size(); i++) {
                for (int j = i + 1; j < restaurants.size(); j++) {
                    Restaurant r1 = restaurants.get(i);
                    Restaurant r2 = restaurants.get(j);

                    int len = Math.min(r1.getAddress().getAddress().length(), r2.getAddress().getAddress().length());
                    final int levenshteinDistance = StringUtils.getLevenshteinDistance(r1.getAddress().getAddress(), r2.getAddress().getAddress());

                    if ((double) levenshteinDistance / (double) len < 0.3) {

                        final AdminUserWrapper adminUserWrapper = new AdminUserWrapper(adminUser);
                        if (!suspect.containsKey(adminUserWrapper)) {

                            suspect.put(adminUserWrapper, new LinkedHashMap<RestaurantWrapper, List<OrderWrapper>>());
                        }

                        suspect.get(adminUserWrapper).put(new RestaurantWrapper(r1), new ArrayList<>(Collections2
                                .transform(adminUserRestaurantOrderMap.get
                                        (adminUser).get(r1), new Function<Order, OrderWrapper>() {
                                    @Override
                                    public OrderWrapper apply(Order input) {
                                        return new OrderWrapper(input);
                                    }
                                })));
                        suspect.get(adminUserWrapper).put(new RestaurantWrapper(r2), new ArrayList<>(Collections2
                                .transform(adminUserRestaurantOrderMap.get
                                        (adminUser).get(r2), new Function<Order, OrderWrapper>() {
                                    @Override
                                    public OrderWrapper apply(Order input) {
                                        return new OrderWrapper(input);
                                    }
                                })));
                        ;
                    }
                }
            }
        }

        return suspect;
    }

    @Transactional(readOnly = true)
    public Map<RestaurantWrapper, List<OrderWrapper>> findSuspectDealer(Date date) {
        OrderQueryRequest request = new OrderQueryRequest();

        Date expected = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addDays(expected, 1);
        Date start = expected;

        request.setStart(start);
        request.setEnd(end);
        request.setPageSize(Integer.MAX_VALUE);
        final Page<Order> orders = orderService.findOrders(request, null);

        Map<Restaurant, List<Order>> restaurantOrderMap = new HashMap<>();
        for (Order order : orders) {
            final Restaurant restaurant = order.getRestaurant();

            if (!restaurantOrderMap.containsKey(restaurant)) {
                restaurantOrderMap.put(restaurant, new ArrayList<Order>());
            }

            restaurantOrderMap.get(restaurant).add(order);
        }

        Map<RestaurantWrapper, List<OrderWrapper>> suspect = new LinkedHashMap<>();
        for (Restaurant restaurant : restaurantOrderMap.keySet()) {
            BigDecimal total = BigDecimal.ZERO;

            int count = 0;
            for (Order order : restaurantOrderMap.get(restaurant)) {
                total = total.add(order.getTotal());

                for (OrderItem oi : order.getOrderItems()) {
                    if (oi.getSku().getProduct().getBrand() != null) {

                        final Long brandId = oi.getSku().getProduct().getBrand().getId();
                        if (Long.valueOf(1).equals(brandId) || Long.valueOf(2).equals(brandId) || Long.valueOf(3).equals(brandId)) {
                            count += oi.getCountQuantity();
                        }
                    }
                }
            }

            if (total.doubleValue() > 2000 && count > 20) {
                suspect.put(new RestaurantWrapper(restaurant), new ArrayList<OrderWrapper>(Collections2.transform(restaurantOrderMap.get
                        (restaurant), new Function<Order, OrderWrapper>() {
                    @Override
                    public OrderWrapper apply(Order input) {
                        return new OrderWrapper(input);
                    }
                })));
            }
        }

        return suspect;
    }


}
