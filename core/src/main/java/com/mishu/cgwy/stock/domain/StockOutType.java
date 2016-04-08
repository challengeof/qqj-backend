package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockOutType {
    ORDER(1, "订单"), TRANSFER(2, "调拨"), PURCHASERETURN(3, "采购退货");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockOutType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockOutType fromInt(int i) {
        switch (i) {
            case 1:
                return ORDER;
            case 2:
                return TRANSFER;
            case 3:
                return PURCHASERETURN;
            default:
                return ORDER;
        }
    }
}
