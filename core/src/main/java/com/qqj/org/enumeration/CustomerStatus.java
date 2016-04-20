package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerStatus {
    STATUS_0((short)0, "待上级审核"),
    STATUS_1((short)1, "待总部审核"),
    STATUS_2((short)2, "审核通过");

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
