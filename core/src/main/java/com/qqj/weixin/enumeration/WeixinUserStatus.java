package com.qqj.weixin.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WeixinUserStatus {
    STATUS_0((short)0, "未审核"),
    STATUS_1((short)1, "审核通过"),
    STATUS_2((short)2, "审核未通过");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private WeixinUserStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static WeixinUserStatus get(Short value) {
        for (WeixinUserStatus i : WeixinUserStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
