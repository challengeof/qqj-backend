package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockAdjustStatus {
    CANCEL(-1, "作废"), PENDINGAUDIT(0, "待审核"), APPROVE(1, "已通过"), REFUSED(2, "已拒绝");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private StockAdjustStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockAdjustStatus fromInt(int i) {
        switch (i) {
            case 0:
                return PENDINGAUDIT;
            case 1:
                return APPROVE;
            case 2:
                return REFUSED;
            case -1:
                return CANCEL;
            default:
                return PENDINGAUDIT;
        }
    }
}
