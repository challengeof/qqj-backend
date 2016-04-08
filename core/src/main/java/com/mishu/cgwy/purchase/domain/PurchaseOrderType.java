package com.mishu.cgwy.purchase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PurchaseOrderType {
    STOCKUP((short) 1, "备货采购"),
    ACCORDING((short) 2, "按需采购");

    private Short value;
    private String name;

    public String getName() {
        return name;
    }
    public Short getValue() {
        return value;
    }

    private PurchaseOrderType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PurchaseOrderType fromInt(int i) {
        switch (i) {
            case 1:
                return STOCKUP;
            case 2:
                return ACCORDING;
            default:
                return STOCKUP;
        }
    }
}
