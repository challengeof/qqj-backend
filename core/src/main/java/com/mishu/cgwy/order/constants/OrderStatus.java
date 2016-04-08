package com.mishu.cgwy.order.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatus {
    CANCEL(-1, "已取消"), UNCOMMITTED(-2,"未提交"), COMMITTED(3,"已下单"), DEALING(-3, "处理中"), SHIPPING(2,"配送中"),COMPLETED(4,"已完成"), RETURNED(5, "已退货");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private OrderStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static OrderStatus fromInt(int i) {
        switch (i) {
            case -2:
                return UNCOMMITTED;
            case 3:
                return COMMITTED;
            case 2:
                return SHIPPING;
            case 4:
                return COMPLETED;
            case 5:
                return RETURNED;
            case -1:
                return CANCEL;
            case -3:
                return DEALING;
            default:
                return UNCOMMITTED;
        }
    }
}
