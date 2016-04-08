package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 11:11 AM
 */
@Data
public class SkuRequest {
    private Long productId;
    private int status = SkuStatus.UNDEFINED.getValue();
//    private boolean bundle;
//    private BigDecimal marketPrice = BigDecimal.ZERO;
    private int capacityInBundle;
    private BigDecimal rate;

    private String singleUnit;
    private BigDecimal singleGross_wight;  //毛重
    private BigDecimal singleNet_weight; //净重
    private BigDecimal singleLong;
    private BigDecimal singleWidth;
    private BigDecimal singleHeight;

    private String bundleUnit;
    private BigDecimal bundleGross_wight;  //毛重
    private BigDecimal bundleNet_weight; //净重
    private BigDecimal bundleLong;
    private BigDecimal bundleWidth;
    private BigDecimal bundleHeight;
}
