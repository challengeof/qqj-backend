package com.mishu.cgwy.coupon.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mishu.cgwy.coupon.wrapper.CouponConstantWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-6-25.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CouponConstant {

    ORDER_WITH_A_GIFT_SEND(5, "满赠优惠券(赠物品)"),
    ORDER_WITH_A_COUPON_SEND(1, "满赠优惠券(赠优惠券)"),
    ACTIVITY_SEND(2, "活动优惠券"),
    REGISTER_SEND(3, "注册优惠券"),
    SHARE_SEND(4, "分享优惠券"),
    PRECISE_SEND(6, "精准优惠券（客服专用）"),
    EXCHANGE_COUPON(7,"兑换优惠劵(赠优惠劵)"),
    TWO_FOR_ONE(8, "买一赠一(赠优惠劵)");

    private Integer type;

    private String name;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private CouponConstant(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static CouponConstant getCouponConstantByType(Integer type) {
        for (CouponConstant couponConstant : CouponConstant.values()) {
            if (couponConstant.type.equals(type)) {
                return couponConstant;
            }
        }

        return null;
    }

    public static List<CouponConstantWrapper> getCouponConstants() {
        List<CouponConstantWrapper> list = new ArrayList<>();
        for (CouponConstant couponConstant : CouponConstant.values()) {
            CouponConstantWrapper couponConstantWrapper = new CouponConstantWrapper();
            couponConstantWrapper.setName(couponConstant.name);
            couponConstantWrapper.setType(couponConstant.type);
            list.add(couponConstantWrapper);
        }
        return  list;
    }
}
