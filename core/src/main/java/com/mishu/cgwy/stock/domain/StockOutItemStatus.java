package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: admin
 * Date: 9/30/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockOutItemStatus {
    UNDISTRIBUTED(0, "未配货"), DISTRIBUTED(1, "已配货"), CANCEL(-1, "取消");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockOutItemStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockOutItemStatus fromInt(int i) {
        switch (i) {
            case 0:
                return UNDISTRIBUTED;
            case 1:
                return DISTRIBUTED;
            case -1:
                return CANCEL;
            default:
                return UNDISTRIBUTED;
        }
    }
}
