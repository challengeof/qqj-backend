package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:28 PM
 */
@Data
public class OrderUpdateRequest {
    private String memo;
    private String newMemo;
    private Integer status;
}
