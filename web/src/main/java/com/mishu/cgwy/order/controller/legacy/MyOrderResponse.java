package com.mishu.cgwy.order.controller.legacy;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 5/22/15
 * Time: 11:00 AM
 */
@Data
@EqualsAndHashCode
public class MyOrderResponse extends RestError {
    private List<OrderWrapper> orders = new ArrayList<>();
}
