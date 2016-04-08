package com.mishu.cgwy.profile.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 7/13/15
 * Time: 6:57 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FeedbackStatus {
    UNPROCESSED(1, "未处理"), SKU_INVALID(2, "在市场中无货"), SKU_VALID(3, "在市场中有货"), AVAILABLE_ONLINE(4, "已上线");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private FeedbackStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static FeedbackStatus fromInt(int i) {
        switch (i) {
            case 1:
                return UNPROCESSED;
            case 2:
                return SKU_INVALID;
            case 3:
                return SKU_VALID;
            case 4:
                return AVAILABLE_ONLINE;

            default:
                return UNPROCESSED;
        }
    }

}
