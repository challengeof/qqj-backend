package com.mishu.cgwy.coupon.constant;

import lombok.Data;

@Data
public class PromotionRuleConstant {

    public static final String PREFIX = "$";

    public static String cityIdRule = PREFIX + "cityId";

    public static String warehouseIdRule = PREFIX + "warehouseId";

    public static String couponTypeRule = PREFIX + "promotionType";

    public static String quantityRule = PREFIX + "quantity";

    public static String descriptionRule = PREFIX + "description";

    public static String discountRule = PREFIX + "discount";

    public static String useRestrictionsTotalMinRule = PREFIX + "useRestrictionsTotalMin";

    public static String useRestrictionsTotalMaxRule = PREFIX + "useRestrictionsTotalMax";

    public static String startRule = PREFIX + "start";

    public static String endRule = PREFIX + "end";

    public static String skuIdRule = PREFIX + "skuId";

    public static String organizationIdRule = PREFIX + "organizationId";

    public static String useRestrictionsCategoryIdsRule = PREFIX + "useRestrictionsCategoryIds";

    public static String promotionPatternRule = PREFIX + "promotionPattern";

    public static String skuUnitRule = PREFIX + "skuUnit";

    public static String buySkuIdRule = PREFIX + "buySkuId";

    public static String buySkuUnitRule = PREFIX + "buySkuUnit";

    public static String buyQuantityRule = PREFIX + "buyQuantity";

    public static String limitedQuantityRule = PREFIX + "limitedQuantity";

    public static String brandIdRule = PREFIX + "brandId";
}
