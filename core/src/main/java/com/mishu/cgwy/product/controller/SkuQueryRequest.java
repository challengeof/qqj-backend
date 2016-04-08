package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 11:11 AM
 */
@Data
public class SkuQueryRequest {
    private Long productId;
    private Long skuId;
    private Long brandId;
    private Integer status;
    private String productName;

    private Long categoryId;
    private Long organizationId;
    private Long skuTagCityId;

    private int page = 0;
    private int pageSize = 100;

}
