package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVendorRequest {
    private Long skuId;
    private Long cityId;
    private Long vendorId;
    private BigDecimal fixedPrice;
    private BigDecimal singleSalePriceLimit;
    private BigDecimal bundleSalePriceLimit;
    private String changeFixedPriceReason;
    private String changeSalePriceLimitReason;
    private Long skuVendorId;
}
