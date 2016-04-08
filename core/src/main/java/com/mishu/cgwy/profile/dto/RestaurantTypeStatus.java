package com.mishu.cgwy.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by challenge on 16/1/20.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantTypeStatus {

    UNDEFINED(1, "未审核"), ACTIVE(2, "生效"), INACTIVE(3, "失效");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private RestaurantTypeStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RestaurantTypeStatus fromInt(int i) {
        switch (i) {
            case 1:
                return UNDEFINED;
            case 2:
                return ACTIVE;
            case 3:
                return INACTIVE;
            default:
                return UNDEFINED;
        }
    }
}
