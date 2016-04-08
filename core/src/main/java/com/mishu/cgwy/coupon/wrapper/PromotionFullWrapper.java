package com.mishu.cgwy.coupon.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.coupon.constant.PromotionConstant;
import com.mishu.cgwy.coupon.constant.PromotionRuleConstant;
import com.mishu.cgwy.coupon.controller.PromotionRequest;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.promotion.domain.Promotion;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bowen on 15/10/27.
 */
@Data
public class PromotionFullWrapper {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Long id;

    private String name;

    private String type;

    private Integer promotionType;

    private Integer promotionPattern;

    private Date start;

    private Date end;

    private String description;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal useRestrictionsTotalMin = BigDecimal.ZERO;

    private BigDecimal useRestrictionsTotalMax = BigDecimal.ZERO;

    private SimpleSkuWrapper sku;

    private int quantity;

    private Long cityId = 0l;

    private Long warehouseId = null;

    private Long organizationId = null;

    private Long skuId;

    private Long[] useRestrictionsCategoryIds;

    private boolean skuUnit;

    private Long buySkuId;

    private Integer buyQuantity;

    private boolean buySkuUnit;

    private Integer limitedQuantity;

    private Long brandId;

    public PromotionFullWrapper() {

    }

    public PromotionFullWrapper(Promotion promotion) throws Exception {
        id = promotion.getId();
        if (promotion.getPromotableItems().getSku() != null) {
            sku = new SimpleSkuWrapper(promotion.getPromotableItems().getSku());
            quantity = promotion.getPromotableItems().getQuantity();
            skuId = sku.getId();
            skuUnit = promotion.getPromotableItems().isBundle();
        }
        start = promotion.getStart();
        end = promotion.getEnd();
        PromotionConstant promotionConstant = PromotionConstant.getPromotionConstantByType(promotion.getType());
        if (promotionConstant != null) {

            promotionType = promotionConstant.getType();
            type = promotionConstant.getName();
        }
        promotionPattern = promotion.getPromotionConstants();
        description = promotion.getDescription();
        discount = promotion.getDiscount();
        limitedQuantity = promotion.getLimitedQuantity();

        PromotionRequest promotionRequest = new ObjectMapper().readValue(promotion.getRuleValue(), PromotionRequest.class);

        cityId = promotionRequest.get(PromotionRuleConstant.cityIdRule);
        warehouseId = promotionRequest.get(PromotionRuleConstant.warehouseIdRule);
        organizationId = promotionRequest.get(PromotionRuleConstant.organizationIdRule);
        useRestrictionsTotalMin = promotionRequest.get(PromotionRuleConstant.useRestrictionsTotalMinRule);
        useRestrictionsTotalMax = promotionRequest.get(PromotionRuleConstant.useRestrictionsTotalMaxRule);
        useRestrictionsCategoryIds = promotionRequest.get(PromotionRuleConstant.useRestrictionsCategoryIdsRule);
        if (promotionConstant.getType().equals(PromotionConstant.TWO_FOR_ONE.getType())) {
            buyQuantity = promotionRequest.get(PromotionRuleConstant.buyQuantityRule);
            buySkuUnit = promotionRequest.get(PromotionRuleConstant.buySkuUnitRule);
            buySkuId = promotionRequest.get(PromotionRuleConstant.buySkuIdRule);
        }
        brandId = promotionRequest.get(PromotionRuleConstant.brandIdRule);
    }


    private String[] getRestrictionsCategoryIds(Map<String, String> subSendRuleFragmentsMap) {
        Pattern pattern = Pattern.compile("OrderService\\.getOrderAmountByCategories\\(order,(.*)\\)");
        for (String key : subSendRuleFragmentsMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String restrictions = matcher.group(1);
                return restrictions.split(",");
            }
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    private Long strToLong(String str) {
        return str == null ? null : Long.valueOf(str);
    }

    private BigDecimal strToBigDecimal(String str) {
        return str == null ? null : new BigDecimal(str);
    }

    private Map<String, String > getSubRuleFragmentsMap(String rule) {
        Map<String, String> subRuleFragmentsMap = new HashMap<>();
        String[] subRules = rule.split("\\|\\||&&");
        for (String subRule : subRules) {
            String[] subRuleFragments = subRule.split("==|>=|<=");
            subRuleFragmentsMap.put(subRuleFragments[0], subRuleFragments[1]);
        }

        return subRuleFragmentsMap;
    }

    private Long getRestrictionsBrandIdAndTotal(Map<String, String> subUseRuleFragmentsMap) {
        Pattern pattern = Pattern.compile("OrderService\\.getOrderAmountByBrands\\(order,?(.*)\\)");
        for (String key : subUseRuleFragmentsMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String restrictionsBrandId = matcher.group(1);
                return Long.valueOf(restrictionsBrandId);
            }
        }
        return null;
    }
}
