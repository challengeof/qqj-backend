package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class ProductOrDynamicPriceQueryRequest {
    private Long id;
    private Long objectId;
    private Long objectType;
    private Long status;

    private int page = 0;
    private int pageSize = 100;

    private String productName;
    private Long cityId;
    private Long organizationId;
    private Long warehouseId;

    private String submitRealName;
    private String checkRealName;

    private Date submitDate;
    private Date passDate;
}
