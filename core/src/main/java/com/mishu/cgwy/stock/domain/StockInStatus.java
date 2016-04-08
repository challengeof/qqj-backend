package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockInStatus {
    UNACCEPTED(0, "未收货"), ACCEPTED(1, "已收货"), CANCEL(-1, "取消");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockInStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockInStatus fromInt(int i) {
        switch (i) {
            case 0:
                return UNACCEPTED;
            case 1:
                return ACCEPTED;
            case -1:
                return CANCEL;
            default:
                return UNACCEPTED;
        }
    }
}
