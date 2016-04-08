package com.mishu.cgwy.coupon.constant;

import lombok.Data;

@Data
public class CouponRuleConstant {

    public static final String PREFIX = "$";

    public static String cityIdRule = PREFIX + "cityId";

    public static String warehouseIdRule = PREFIX + "warehouseId";

    public static String nameRule = PREFIX + "name";

    public static String couponTypeRule = PREFIX + "couponType";

    public static String quantityRule = PREFIX + "quantity";

    public static String descriptionRule = PREFIX + "description";

    public static String discountRule = PREFIX + "discount";

    public static String remarkRule = PREFIX + "remark";

    public static String sendRestrictionsTotalMinRule = PREFIX + "sendRestrictionsTotalMin";

    public static String sendRestrictionsTotalMaxRule = PREFIX + "sendRestrictionsTotalMax";

    public static String useRestrictionsTotalRule = PREFIX + "useRestrictionsTotal";

    public static String startRule = PREFIX + "start";

    public static String endRule = PREFIX + "end";

    public static String skuIdRule = PREFIX + "skuId";

    public static String restaurantsRule = PREFIX + "restaurants";

    public static String sendRestrictionsCategoryIdsRule = PREFIX + "sendRestrictionsCategoryIds";

    public static String useRestrictionsCategoryIdsRule = PREFIX + "useRestrictionsCategoryIds";

    public static String deadlineRule = PREFIX + "deadline";

    public static String periodOfValidityRule = PREFIX + "periodOfValidity";

    public static String scoreRule = PREFIX + "score";

    public static String sendCouponQuantityRule = PREFIX + "sendCouponQuantity";

    public static String buyQuantityRule = PREFIX + "buyQuantity";

    public static String buySkuUnitRule = PREFIX + "buySkuUnit";

    public static String buySkuIdRule = PREFIX + "buySkuId";

    public static String beginningDaysRule = PREFIX + "beginningDays";

    public static String brandIdRule = PREFIX + "brandId";

    public static String couponRestrictionRule = PREFIX + "couponRestriction";
}
