package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockInType {
    PURCHASE(1, "采购"), RETURN(2, "退货"), TRANSFER(3, "调拨");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockInType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockInType fromInt(int i) {
        switch (i) {
            case 1:
                return PURCHASE;
            case 2:
                return RETURN;
            case 3:
                return TRANSFER;
            default:
                return PURCHASE;
        }
    }
}
