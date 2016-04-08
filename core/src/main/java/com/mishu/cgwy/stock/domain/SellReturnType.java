package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellReturnType {
    CURRENT(1, "当期"), PAST(2, "往期");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SellReturnType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SellReturnType fromInt(int i) {
        switch (i) {
            case 1:
                return CURRENT;
            case 2:
                return PAST;
            default:
                return CURRENT;
        }
    }
}
