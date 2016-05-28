package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerStage {
    STAGE_1((short)1, "上级"),
    STAGE_2((short)2, "创始人"),
    STAGE_3((short)3, "总部");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CustomerStage(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CustomerStage get(Short value) {
        for (CustomerStage i : CustomerStage.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
