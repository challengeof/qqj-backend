package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15/12/1.
 */
@Data
public class OrderRequest {

    private Long skuId;
    private Integer quantity;
    private boolean bundle;
    private BigDecimal price;
}
