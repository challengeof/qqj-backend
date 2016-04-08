package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.order.dto.OrderStatistics;
import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class OrderQueryResponse {
    private long total;
    private int page;
    private int pageSize;

    private List<SimpleOrderWrapper> orders;
    private OrderStatistics orderStatistics;

}
