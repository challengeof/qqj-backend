package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by admin on 2015/10/11.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountPayableStatus {

    UNWRITEOFF((short)0, "未销账"), PARTWRITEOFF((short)1, "部分销"), WRITEOFF((short)2, "已销账");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private AccountPayableStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountPayableStatus fromInt(int i) {
        switch (i) {
            case 0:
                return UNWRITEOFF;
            case 1:
                return PARTWRITEOFF;
            case 2:
                return WRITEOFF;
            default:
                return UNWRITEOFF;
        }
    }
}
