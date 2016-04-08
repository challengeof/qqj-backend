package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransferStatus {

    CANSELED((short) 0, "已取消"),
    NOTCOMMITTED((short) 1, "未提交"),
    PENDINGAUDIT((short) 2, "待审核"),
    EXECUTION((short) 3, "执行中"),
    COMPLETED((short) 4, "已收货");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private TransferStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static TransferStatus fromInt(int i) {
        switch (i) {
            case 0:
                return CANSELED;
            case 1:
                return NOTCOMMITTED;
            case 2:
                return PENDINGAUDIT;
            case 3:
                return EXECUTION;
            case 4:
                return COMPLETED;
            default:
                return NOTCOMMITTED;
        }
    }
}
