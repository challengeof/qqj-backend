package com.mishu.cgwy.accounting.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by admin on 2015/10/11.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AccountReceivableType {

    SELL(0, "销售"), RETURN(1, "退货");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private AccountReceivableType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AccountReceivableType fromInt(int i) {
        switch (i) {
            case 0:
                return SELL;
            case 1:
                return RETURN;
            default:
                return SELL;
        }
    }
}
