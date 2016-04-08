package com.mishu.cgwy.purchase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PurchaseOrderStatus {
    CANSELED((short)0, "已取消"),
    NOTCOMMITTED((short)1, "未提交"),
    PENDINGAUDIT((short)2, "待审核"),
    EXECUTION((short)3, "执行中"),
    COMPLETED((short)4, "已收货");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private PurchaseOrderStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PurchaseOrderStatus get(Short value) {
        for (PurchaseOrderStatus i : PurchaseOrderStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
