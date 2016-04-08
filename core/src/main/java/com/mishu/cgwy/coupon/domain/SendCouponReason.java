package com.mishu.cgwy.coupon.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SendCouponReason {

    REASON1((short)1, "散品重量不够"),
    REASON2((short)2, "送货延迟"),
    REASON3((short)3, "商品破损"),
    REASON4((short)4, "产品质量问题不退货"),
    REASON5((short)5, "未确定产品质量问题不退货"),
    REASON6((short)6, "服务态度投诉"),
    REASON7((short)7, "客户承诺未兑现"),
    REASON8((short)8, "奖品/奖券兑换"),
    REASON9((short)9, "回馈客户"),
    REASON10((short)10, "其他");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SendCouponReason(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SendCouponReason from(Short i) {
        for (SendCouponReason reason : SendCouponReason.values()) {
            if (reason.getValue().compareTo(i) == 0) {
                return reason;
            }
        }
        return null;
    }
}
