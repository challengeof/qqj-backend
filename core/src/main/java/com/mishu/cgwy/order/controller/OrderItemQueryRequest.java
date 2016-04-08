package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:05 PM
 */
@Data
public class OrderItemQueryRequest {
    private Date start;
    private Date end;
    private Long orderId;
    private Integer orderStatus;
    private Long skuId;
    private Long restaurantId;

    private String productName;
    private String restaurantName;
    private Long warehouseId;
    private Long cityId;
    private Long organizationId;

    private Long orderType;
    private int page = 0;
    private int pageSize = 100;
}
