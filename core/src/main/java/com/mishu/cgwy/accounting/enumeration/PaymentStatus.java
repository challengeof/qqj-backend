package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/12.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentStatus {
    NORMAL((short) 1, "正常"),
    CANCELED((short) 0, "取消");

    private Short value;
    private String name;

    public String getName() {
        return name;
    }
    public Short getValue() {
        return value;
    }

    private PaymentStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PaymentStatus fromInt(int i) {
        switch (i) {
            case 0:
                return CANCELED;
            case 1:
                return NORMAL;
            default:
                return null;
        }
    }
}
