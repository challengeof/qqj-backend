package com.mishu.cgwy.task.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskStatus {
    CANCELED((short)0, "已取消"),
    TOBEEXECUTED((short)1, "待执行"),
    EXECUTION((short)2, "执行中"),
    SUCCESS((short)3, "已执行"),
    FAIL((short)4, "执行失败");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private TaskStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static TaskStatus get(Short value) {
        for (TaskStatus i : TaskStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
