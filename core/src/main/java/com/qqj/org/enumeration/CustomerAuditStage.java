package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerAuditStage {
    STAGE_1((short)1, "直属总代"),
    STAGE_2((short)2, "总部");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CustomerAuditStage(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CustomerAuditStage get(Short value) {
        for (CustomerAuditStage i : CustomerAuditStage.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
