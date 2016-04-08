package com.mishu.cgwy.profile.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/12/18.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FeedbackType {
    CUSTOMER((short)1, "用户"),
    VENDOR((short)2, "供应商");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private FeedbackType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static FeedbackType get(Short value) {
        for (FeedbackType i : FeedbackType.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
