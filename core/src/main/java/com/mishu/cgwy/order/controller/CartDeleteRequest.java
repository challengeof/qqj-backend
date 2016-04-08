package com.mishu.cgwy.order.controller;

import lombok.Data;

/**
 * User: wangwei
 * Date: 9/25/15
 * Time: 11:06 AM
 */
@Data
public class CartDeleteRequest {
    private Long skuId;
    private boolean bundle;
}
