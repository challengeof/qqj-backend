package com.mishu.cgwy.purchase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/10/10.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReturnNoteStatus {
    CANSELED((short) -1, "已取消"),
    REJECTED((short)0, "已拒绝"),
    PENDINGAUDIT((short)1, "待审核"),
    AUDITED((short)2, "已审核"),
    COMPLETED((short)3, "已退货");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private ReturnNoteStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ReturnNoteStatus get(Short value) {
        for (ReturnNoteStatus i : ReturnNoteStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
