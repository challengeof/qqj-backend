package com.qqj.weixin.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WeixinPicType {
    Type_1((short)1, "素颜照片"),
    Type_2((short)2, "妆后照片");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private WeixinPicType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static WeixinPicType get(Short value) {
        for (WeixinPicType i : WeixinPicType.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
