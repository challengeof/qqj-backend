package com.mishu.cgwy.purchase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PurchaseOrderPrint {
    NOTYET(Boolean.FALSE, "未打印"),
    HASBEEN(Boolean.TRUE, "已打印");

    private Boolean value;
    private String name;

    public Boolean getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private PurchaseOrderPrint(Boolean value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PurchaseOrderPrint get(Boolean value) {
        for (PurchaseOrderPrint i : PurchaseOrderPrint.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
