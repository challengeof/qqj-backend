package com.qqj.org.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerAuditStatus {
    PASS((short)0, "审核通过"),
    WAITING_CHIEF((short)1, "待上级审核"),
    WAITING_TEAM_LEADER((short)2, "待创始人审批"),
    WAITING_HQ((short)3, "待总部审核"),
    CHIEF_REJECT((short)-1, "上级拒绝"),
    TEAM_LEADER_REJECT((short)-2, "创始人拒绝"),
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
