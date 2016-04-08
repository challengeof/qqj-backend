package com.mishu.cgwy.order.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.constant.CouponRuleConstant;
import com.mishu.cgwy.coupon.controller.CouponRequest;
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
public class CouponWrapper {

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

    private BigDecimal sendRestrictionsTotalMin = BigDecimal.ZERO;

    private BigDecimal sendRestrictionsTotalMax = BigDecimal.ZERO;

    private BigDecimal useRestrictionsTotal = BigDecimal.ZERO;

    private SimpleSkuWrapper sku;

    private int quantity;

    private Long cityId = 0l;

    private Long warehouseId = null;

//    private Long organizationId = null;

    private Long skuId;

    private Long[] sendRestrictionsCategoryIds;

    private Long[] useRestrictionsCategoryIds;

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

    public CouponWrapper() {

    }

    public CouponWrapper(Coupon coupon) throws Exception {
        id = coupon.getId();
        if (coupon.getCreateTime() != null) {
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

        CouponRequest couponRequest = new ObjectMapper().readValue(coupon.getRuleValue(), CouponRequest.class);

        cityId = couponRequest.get(CouponRuleConstant.cityIdRule);
        warehouseId = couponRequest.get(CouponRuleConstant.warehouseIdRule);
        if (!couponType.equals(CouponConstant.EXCHANGE_COUPON.getType())) {
            sendRestrictionsTotalMin = couponRequest.getSendRestrictionsTotalMin();
            sendRestrictionsTotalMax = couponRequest.getSendRestrictionsTotalMax();
            sendRestrictionsCategoryIds = couponRequest.get(CouponRuleConstant.sendRestrictionsCategoryIdsRule);
        }

        if (couponType.equals(CouponConstant.TWO_FOR_ONE.getType())) {
            buyQuantity = couponRequest.get(CouponRuleConstant.buyQuantityRule);
            buySkuUnit = couponRequest.get(CouponRuleConstant.buySkuUnitRule);
            buySkuId = couponRequest.get(CouponRuleConstant.buySkuIdRule);
            sendCouponQuantity = couponRequest.get(CouponRuleConstant.sendCouponQuantityRule);
        }

        useRestrictionsTotal = couponRequest.get(CouponRuleConstant.useRestrictionsTotalRule);
        useRestrictionsCategoryIds = couponRequest.get(CouponRuleConstant.useRestrictionsCategoryIdsRule);
        brandId = couponRequest.get(CouponRuleConstant.brandIdRule);
    }
}
