package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by admin on 2015/10/11.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountReceivableStatus {

    UNWRITEOFF(0, "未销账"), WRITEOFF(1, "已销账");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private AccountReceivableStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountReceivableStatus fromInt(int i) {
        switch (i) {
            case 0:
                return UNWRITEOFF;
            case 1:
                return WRITEOFF;
            default:
                return UNWRITEOFF;
        }
    }
}
