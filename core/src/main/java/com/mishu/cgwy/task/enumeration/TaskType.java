package com.mishu.cgwy.task.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskType {
    EXCELEXPORT((short)1, "excel导出"),
    EXCELIMPORT((short)2, "excel导入");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private TaskType(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static TaskType get(Short value) {
        for (TaskType i : TaskType.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
