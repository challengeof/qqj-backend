package com.mishu.cgwy.purchase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PurchaseOrderItemStatus {
    INVALID((short)0, "已取消"),
    TOBEEXECUTED((short)1, "待执行"),
    EXECUTION((short)2, "执行中"),
    COMPLETED((short)3, "已完成");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private PurchaseOrderItemStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PurchaseOrderItemStatus get(Short value) {
        for (PurchaseOrderItemStatus i : PurchaseOrderItemStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
