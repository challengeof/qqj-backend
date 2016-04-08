package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.constant.CouponRuleConstant;
import lombok.Data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CouponRequest {
    private Long cityId;

    private Long warehouseId;

    private String name;

    private Integer couponType;

    private Integer couponRestriction;

    private Integer quantity;

    private String description;

    private BigDecimal discount;

    private String remark;

    private BigDecimal sendRestrictionsTotalMin;

    private BigDecimal sendRestrictionsTotalMax;

    private BigDecimal useRestrictionsTotal;

    private Date start;

    private Date end;

    private Long skuId;

    private List<Long> restaurants;

    private Long[] sendRestrictionsCategoryIds;

    private Long[] useRestrictionsCategoryIds;

    private Date deadline;

    private Integer periodOfValidity;

    private Long score;

    private int sendCouponQuantity;

    private int buyQuantity;

    private boolean buySkuUnit;

    private Long buySkuId;

    private int beginningDays;

    private Long brandId;

    public <T> T get(String key) throws Exception {
        String realKey = key.replaceFirst("\\" + CouponRuleConstant.PREFIX, "");
        Field field = CouponRequest.class.getDeclaredField(realKey);
        field.setAccessible(true);
        return (T)field.get(this);
    }
}
