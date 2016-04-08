package com.mishu.cgwy.order.controller;

import lombok.Data;

/**
 * Created by wangwei on 15/10/14.
 */
@Data
public class SellCancelItemRequest {

    private Long orderItemId;
    private int quantity;
    private Long skuId;
    private Boolean bundle;
    private int reasonId;
    private String memo;
}
