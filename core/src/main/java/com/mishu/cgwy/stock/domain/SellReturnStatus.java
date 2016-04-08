package com.mishu.cgwy.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User: admin
 * Date: 9/22/15
 * Time: 12:42 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellReturnStatus {
    PENDINGAUDIT(0, "待审核"), EXECUTION(1, "执行中"), REFUSED(2, "已拒绝"), COMPLETED(3, "已收货");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SellReturnStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SellReturnStatus fromInt(int i) {
        switch (i) {
            case 0:
                return PENDINGAUDIT;
            case 1:
                return EXECUTION;
            case 2:
                return REFUSED;
            case 3:
                return COMPLETED;
            default:
                return PENDINGAUDIT;
        }
    }
}
