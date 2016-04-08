package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockOutStatus {

    CANCEL(-1, "取消"), IN_STOCK(0, "未出库"), HAVE_OUTBOUND(1, "已出库"), FINISHED(2, "已完成");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockOutStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockOutStatus fromInt(int i) {
        switch (i) {
            case 0:
                return IN_STOCK;
            case 1:
                return HAVE_OUTBOUND;
            case -1:
                return CANCEL;
            case 2:
                return FINISHED;
            default:
                return IN_STOCK;
        }
    }
}
