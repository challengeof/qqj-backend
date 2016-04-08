package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.constant.PromotionRuleConstant;
import lombok.Data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bowen on 15/10/27.
 */
@Data
public class PromotionRequest {

    private Long cityId;

    private Long warehouseId;

    private Integer promotionType;

    private Integer quantity;

    private String description;

    private BigDecimal discount;

    private BigDecimal useRestrictionsTotalMin;

    private BigDecimal useRestrictionsTotalMax;

    private Date start;

    private Date end;

    private Long skuId;

    private Long organizationId;

    private Long[] useRestrictionsCategoryIds;

    private Integer promotionPattern;

    private boolean skuUnit;

    private Long buySkuId;

    private boolean buySkuUnit;

    private Integer buyQuantity;

    private Integer limitedQuantity;

    private Long brandId;

    public <T> T get(String key) throws Exception {
        String realKey = key.replaceFirst("\\" + PromotionRuleConstant.PREFIX, "");
        Field field = getClass().getDeclaredField(realKey);
        field.setAccessible(true);
        return (T)field.get(this);
    }
}
