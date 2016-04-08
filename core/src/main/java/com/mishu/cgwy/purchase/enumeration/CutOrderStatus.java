package com.mishu.cgwy.purchase.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/28.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CutOrderStatus {
    NOTSTARTED((short) 0, "已截单未录入"),
    NOTCOMMITED((short) 1, "已截单未提交"),
    COMMITED((short)2, "外采单已录入并提交");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CutOrderStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CutOrderStatus get(Short value) {
        for (CutOrderStatus i : CutOrderStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
