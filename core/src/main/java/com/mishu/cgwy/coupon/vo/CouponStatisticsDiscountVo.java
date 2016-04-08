package com.mishu.cgwy.coupon.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by king-ck on 2015/12/16.
 */
@Data
public class CouponStatisticsDiscountVo {

    private Long cityId;
    private String cityName;
    private Long warehouseId;
    private String warehouseName;

    private BigDecimal discountAmount;
//    private BigDecimal prePeriodBalance;//余额
//    private BigDecimal crtPeriodSendAmount;//发放金额
//    private BigDecimal crtPeriodUsedAmount;//使用金额
//    private BigDecimal crtPeriodOverdue;//过期金额


    public CouponStatisticsDiscountVo(Long cityId, String cityName, Long warehouseId, String warehouseName, BigDecimal discountAmount) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.discountAmount = discountAmount;
    }
}