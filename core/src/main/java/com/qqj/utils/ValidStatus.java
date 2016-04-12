package com.qqj.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by xiao1zhao2 on 16/1/7.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ValidStatus {

    INVALID(0, "无效"), VALID(1, "有效");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    ValidStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ValidStatus fromInt(int i) {
        switch (i) {
            case 0:
                return INVALID;
            case 1:
                return VALID;
            default:
                return VALID;
        }
    }
}
