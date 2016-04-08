package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.order.wrapper.OrderGroupWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/8/25.
 */
@Data
public class OrderGroupsSku{

    private OrderGroupWrapper orderGroupWrapper;

    private List<OrderGroupsSkuTotal> orderGroupsSkuTotals = new ArrayList<>();

}
