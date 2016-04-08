package com.mishu.cgwy.order.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellCancelReason {


    ORDER_ERROR(1, "订错/订多"), SELF_PURCHASE(2, "已自己采购"), EXPENSIVE(3, "价格贵"),
    DELIVERY_INCONVENIENT(4, "送货时间不方便接货"), PARTICIPATE_ACTIVITY(5,"凑钱参加活动"),
    NOT_CONVENIENT_RECEIVE_GOODS(6,"临时有事,不方便接货"),NOT_WANT_TO_BUY(7,"不想买了"),OTHER_REASON(8, "其他原因");


    private Integer value;
    private String name;

    private SellCancelReason(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }



    public static SellCancelReason fromInt(int i) {
        switch (i) {
            case 1:
                return ORDER_ERROR;
            case 2:
                return SELF_PURCHASE;
            case 3:
                return EXPENSIVE;
            case 4:
                return DELIVERY_INCONVENIENT;
            case 5:
                return PARTICIPATE_ACTIVITY;
            case 6:
                return NOT_CONVENIENT_RECEIVE_GOODS;
            case 7:
                return NOT_WANT_TO_BUY;
            case 8:
                return OTHER_REASON;
            default:
                return OTHER_REASON;
        }
    }
}
