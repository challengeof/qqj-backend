package com.mishu.cgwy.product.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/15/15
 * Time: 10:34 AM
 */
@Data
public class DynamicPriceRequest {

    private Long id;
    private Long vendorId;
    private Long skuId;
    private Long warehouseId;

    private BigDecimal singleSalePrice;
    private boolean singleAvailable;
    private boolean singleInSale;

    private BigDecimal bundleSalePrice;
    private boolean bundleAvailable;
    private boolean bundleInSale;

    private boolean effectType;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date effectTime;
}
