package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/19.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountPayableWriteOffStatus {
    NORMAL((short) 1, "销账成功"),
    CANCELED((short) 0, "已作废");

    private Short value;
    private String name;

    public String getName() {
        return name;
    }
    public Short getValue() {
        return value;
    }

    private AccountPayableWriteOffStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountPayableWriteOffStatus from(Short value) {
        switch(value) {
            case 0:
                return CANCELED;
            case 1:
                return NORMAL;
            default:
                return null;
        }
    }
}
