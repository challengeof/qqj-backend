package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bowen on 15-6-24.
 */
@Data
public class OriginCouponWrapper {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Long id;

    private String name;

    private String type;

    private Integer couponType;

    private Date start;

    private Date end;

    private String description;

    private String remark;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal sendRestrictionsTotal = BigDecimal.ZERO;

    private BigDecimal useRestrictionsTotal = BigDecimal.ZERO;

    private SimpleSkuWrapper sku;

    private int quantity;

    private Long cityId = 0l;

    private Long warehouseId = null;

//    private Long organizationId = null;

    private Long skuId;

    private String[] sendRestrictionsCategoryIds;

    private String[] useRestrictionsCategoryIds;

    private String createTime;

    private Date deadline;

    private Integer periodOfValidity;

    private Long score;

    private int sendCouponQuantity;

    private Long buySkuId;

    private int buyQuantity;

    private boolean buySkuUnit;

    private int beginningDays;

    private Long brandId;

    private Integer couponRestriction;

    public OriginCouponWrapper() {

    }

    public OriginCouponWrapper(Coupon coupon) {
        id = coupon.getId();
        if(coupon.getCreateTime() != null) {
            createTime = dateFormat.format(coupon.getCreateTime());
        }
        if (coupon.getSku() != null) {
            sku = new SimpleSkuWrapper(coupon.getSku());
            quantity = coupon.getQuantity();
            skuId = sku.getId();
        }
        name = coupon.getName();
        start = coupon.getStart();
        if (coupon.getEnd() != null) {
            end = DateUtils.truncate(coupon.getEnd(), Calendar.DATE);
        }
        if (coupon.getDeadline() != null) {
            deadline = DateUtils.truncate(coupon.getDeadline(), Calendar.DATE);
        }
        this.periodOfValidity = coupon.getPeriodOfValidity();
        CouponConstant couponConstant = CouponConstant.getCouponConstantByType(coupon.getCouponConstants());
        couponType = couponConstant.getType();
        type = couponConstant.getName();
        description = coupon.getDescription();
        discount = coupon.getDiscount();
        remark = coupon.getRemark();
        score = coupon.getScore();
        beginningDays = coupon.getBeginningDays();
        couponRestriction = coupon.getCouponRestriction();

        String sendRule = coupon.getSendRule();

        if (!Boolean.TRUE.toString().equals(sendRule) && !Boolean.FALSE.toString().equals(sendRule) ) {
            Map<String, String> subSendRuleFragmentsMap = getSubRuleFragmentsMap(sendRule);
            cityId = strToLong(subSendRuleFragmentsMap.get("city.id"));
            warehouseId = strToLong(subSendRuleFragmentsMap.get("warehouse.id"));
            if (!couponType.equals(CouponConstant.EXCHANGE_COUPON.getType())) {
                Object[] restrictionsCategoryIdsAndTotal = getRestrictionsCategoryIdsAndTotal(subSendRuleFragmentsMap);

                if (restrictionsCategoryIdsAndTotal != null && restrictionsCategoryIdsAndTotal.length == 2) {
                    sendRestrictionsTotal = strToBigDecimal((String)restrictionsCategoryIdsAndTotal[1]);
                    sendRestrictionsCategoryIds = (String[])restrictionsCategoryIdsAndTotal[0];
                }

            }

            if (couponType.equals(CouponConstant.TWO_FOR_ONE.getType())) {

                String str = subSendRuleFragmentsMap.get("true");

                String[] strings = str.substring(str.indexOf(",") + 1, str.lastIndexOf(")")).split(",");
                buyQuantity = Integer.valueOf(strings[0]);
                buySkuUnit = Boolean.valueOf(strings[1]);
                buySkuId = strToLong(strings[2]);
                sendCouponQuantity = coupon.getSendCouponQuantity();
            }
        }

        String useRule = coupon.getUseRule();
        if (!Boolean.TRUE.toString().equals(useRule) && !Boolean.FALSE.toString().equals(useRule)) {
            Map<String, String> subUseRuleFragmentsMap = getSubRuleFragmentsMap(useRule);
//            organizationId = strToLong(subUseRuleFragmentsMap.get("organization.id"));
            Object[] restrictionsCategoryIdsAndTotal = getRestrictionsCategoryIdsAndTotal(subUseRuleFragmentsMap);
            useRestrictionsTotal = strToBigDecimal((String)restrictionsCategoryIdsAndTotal[1]);
            useRestrictionsCategoryIds = (String[])restrictionsCategoryIdsAndTotal[0];
            brandId = getRestrictionsBrandIdAndTotal(subUseRuleFragmentsMap);

        }
    }

    private Object[] getRestrictionsCategoryIdsAndTotal(Map<String, String> subSendRuleFragmentsMap) {
        Pattern pattern = Pattern.compile("OrderService\\.getOrderAmountByCategories\\(order,?(.*)\\)");
        for (String key : subSendRuleFragmentsMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String restrictionsCategoryIds = matcher.group(1);
                return new Object[]{restrictionsCategoryIds.split(","), subSendRuleFragmentsMap.get(key)};
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

    private Long getRestrictionsBrandIdAndTotal(Map<String, String> subSendRuleFragmentsMap) {
        Pattern pattern = Pattern.compile("OrderService\\.getOrderAmountByBrands\\(order,?(.*)\\)");
        for (String key : subSendRuleFragmentsMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String restrictionsBrandId = matcher.group(1);
                return Long.valueOf(restrictionsBrandId);
            }
        }
        return null;
    }
}
