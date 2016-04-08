package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by admin on 2015/10/20.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum VendorAccountOperationType {

    PAYABLE((short)0, "应付"), PAYMENT((short)1, "付款"), WRITEOFF((short)2, "核销"),;

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private VendorAccountOperationType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static VendorAccountOperationType from(Short i) {
        switch (i) {
            case 0:
                return PAYABLE;
            case 1:
                return PAYMENT;
            case 2:
                return WRITEOFF;
            default:
                return null;
        }
    }
}
