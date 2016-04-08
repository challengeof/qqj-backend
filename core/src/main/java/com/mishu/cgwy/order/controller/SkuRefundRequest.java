package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15/8/6.
 */
@Deprecated
@Data
public class SkuRefundRequest {

    private Long skuId;
    private int quantity;
    private boolean bundle;
    private BigDecimal price;
    private int saleQuantity;
    private String name;
}
