package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellCancelType {
    CUSTOMER_CANCEL(1, "客服取消"), DEPOT_CANCEL(2, "缺货取消"), CUSTOMER_SELF_CANCEL(3, "客户自己取消");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SellCancelType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SellCancelType fromInt(int i) {
        switch (i) {
            case 1:
                return CUSTOMER_CANCEL;
            case 2:
                return DEPOT_CANCEL;
            case 3:
                return CUSTOMER_SELF_CANCEL;
            default:
                return DEPOT_CANCEL;
        }
    }
}
