package com.mishu.cgwy.product.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 11:33 PM
 */
@Data
public class DynamicPriceQueryRequest {
    private Long productId;
    private Long skuId;
    private String productName;
    private Long warehouseId;
    private Long brandId;
    private Long categoryId;
    private Long cityId;
    private Long organizationId;
    private Integer status;
    private Boolean singleAvailable;
    private Boolean singleInSale;
    private Boolean bundleAvailable;
    private Boolean bundleInSale;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date skuCreateDate;

    private int page = 0;
    private int pageSize = 100;
}
