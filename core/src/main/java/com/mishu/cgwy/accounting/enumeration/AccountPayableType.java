package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by admin on 2015/10/11.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountPayableType {

    PURCHASE((short)0, "采购"), RETURN((short)1, "退货");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private AccountPayableType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountPayableType fromInt(Short i) {
        switch (i) {
            case 0:
                return PURCHASE;
            case 1:
                return RETURN;
            default:
                return PURCHASE;
        }
    }
}
