package com.mishu.cgwy.order.controller;

import lombok.Data;

/**
 * Created by wangwei on 15/10/14.
 */
@Data
public class SellReturnItemRequest {

    private Long orderItemId;
    private int quantity; //数量
    private Long reasonId;//原因
    private String memo; //备注
}
