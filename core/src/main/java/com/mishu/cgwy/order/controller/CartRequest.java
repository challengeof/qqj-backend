package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.order.constants.CartSkuType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * User: xudong
 * Date: 5/28/15
 * Time: 1:24 PM
 */
@Data
public class CartRequest {
    private Long skuId;
    private boolean bundle;

    private Integer quantity;

    private Long spikeItemId;
    private Integer cartSkuType; // 对应  CartSkuType 枚举

}
