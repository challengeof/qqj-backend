package com.mishu.cgwy.message;

/**
 * Created by wangguodong on 15/8/7.
 */
public enum CouponSenderEnum {
    CUSTOMER_COUPON_SEND(CustomerCouponSender.class),
    COMPLETE_ORDER_SEND(CompleteOrderCouponSender.class),
    GATHERING_COMPLETED_SEND(GatheringCompletedCouponSender.class),
    ACTIVITY_SEND(ActivityCouponSender.class),
    REGISTER_SEND(RegisterCouponSender.class);

    private Class<? extends CouponSender> couponSenderClass;

    public Class<? extends CouponSender> getCouponSenderClass() {
        return couponSenderClass;
    }

    private CouponSenderEnum(Class<? extends CouponSender> couponSenderClass) {
        this.couponSenderClass = couponSenderClass;
    }
}
