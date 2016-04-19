package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerStatus {
    CANCELED((short)0, "总代"),
    TOBEEXECUTED((short)1, "一级代理"),
    EXECUTION((short)2, "二级代理");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CustomerStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CustomerStatus get(Short value) {
        for (CustomerStatus i : CustomerStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
