package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerAuditStatus {
    PASS((short)0, "审核通过"),
    WAITING_DIRECT_LEADER((short)1, "直属总代审批中"),
    WAITING_HQ((short)2, "总部审批中"),
    DIRECT_LEADER_REJECT((short)-1, "直属总代拒绝"),
    HQ_REJECT((short)-2, "总部拒绝");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CustomerAuditStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CustomerAuditStatus get(Short value) {
        for (CustomerAuditStatus i : CustomerAuditStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
