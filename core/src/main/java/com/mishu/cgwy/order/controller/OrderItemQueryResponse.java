package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.order.wrapper.OrderItemWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:06 PM
 */
@Data
public class OrderItemQueryResponse {
    private long total;

    private int page;
    private int pageSize;

    private List<OrderItemWrapper> orderItems = new ArrayList<OrderItemWrapper>();

}
