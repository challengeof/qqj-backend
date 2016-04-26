package com.qqj.weixin.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WeixinUserGroup {
    Group_1((short)1, null, "1970-01-01", "60组"),
    Group_2((short)2, "1970-01-01", "1980-01-01", "70组"),
    Group_3((short)3, "1980-01-01", "1990-01-01", "80组"),
    Group_4((short)4, "1990-01-01", null, "90组");

    private Short value;
    private String start;
    private String end;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    private WeixinUserGroup(Short value, String start, String end, String name) {
        this.value = value;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public static WeixinUserGroup get(Short value) {
        for (WeixinUserGroup i : WeixinUserGroup.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
