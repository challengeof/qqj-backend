package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/19.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountReceivableWriteOffStatus {
    VALID((short) 1, "有效"),
    INVALID((short) -1, "无效");

    private Short value;
    private String name;

    public String getName() {
        return name;
    }

    public Short getValue() {
        return value;
    }

    AccountReceivableWriteOffStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountReceivableWriteOffStatus fromInt(int value) {
        switch (value) {
            case 1:
                return VALID;
            case -1:
                return INVALID;
            default:
                return VALID;
        }
    }
}
