package com.mishu.cgwy.order.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 15/9/15.
 */
@Deprecated
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RefundReasonStatus {
    BREAKAGE(1,"商品破损"), QUALITY_PROBLEM(2,"质量问题"),MISSEND(3,"送错货"), NOT_AS_DESCRIBED(4,"产品与描述不符"), DELIVERY_TIME_WRONG(5, "未按约定时间送货"), WITHOUT_REASON(6, "无理由退货"),;

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private RefundReasonStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RefundReasonStatus fromInt(int i) {
        switch (i) {
            case 1:
                return BREAKAGE;
            case 2:
                return QUALITY_PROBLEM;
            case 3:
                return MISSEND;
            case 4:
                return NOT_AS_DESCRIBED;
            case 5:
                return DELIVERY_TIME_WRONG;
            case 6:
                return WITHOUT_REASON;
            default:
                return WITHOUT_REASON;
        }
    }
}
