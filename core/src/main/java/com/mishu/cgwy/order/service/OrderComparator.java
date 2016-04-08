package com.mishu.cgwy.order.service;

import com.mishu.cgwy.order.domain.Order;

import java.util.Comparator;

/**
 * Created by kaicheng on 3/27/15.
 */
public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order order1, Order order2) {
        return order1.getSubmitDate().compareTo(order2.getSubmitDate());
    }
}
