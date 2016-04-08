package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by xiao1zhao2 on 15/11/5.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockPrintStatus {
    NOTYET(Boolean.FALSE, "未打印"), HASBEEN(Boolean.TRUE, "已打印");

    private Boolean value;
    private String name;

    public Boolean getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    StockPrintStatus(Boolean value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StockPrintStatus fromBoolean(Boolean b) {
        for (StockPrintStatus status : StockPrintStatus.values()) {
            if (status.value.equals(b)) {
                return status;
            }
        }
        return NOTYET;
    }
}
