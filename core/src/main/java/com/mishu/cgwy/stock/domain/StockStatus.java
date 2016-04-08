package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockStatus {
    AVAILABLE(0, "可用"), OCCUPIED(1, "占用"), ONROAD(2, "调拨在途");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockStatus fromInt(int i) {
        switch (i) {
            case 0:
                return AVAILABLE;
            case 1:
                return OCCUPIED;
            case 2:
                return ONROAD;
            default:
                return AVAILABLE;
        }
    }
}
