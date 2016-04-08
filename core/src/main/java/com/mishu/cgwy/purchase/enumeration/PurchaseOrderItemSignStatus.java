package com.mishu.cgwy.purchase.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/28.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PurchaseOrderItemSignStatus {
    NOTREADY((short) 0, "未标记"),
    READY((short) 1, "已标记");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private PurchaseOrderItemSignStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PurchaseOrderItemSignStatus get(Short value) {
        for (PurchaseOrderItemSignStatus i : PurchaseOrderItemSignStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
