package com.mishu.cgwy.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SystemEmailType {
    NOTRECEIVE(1, "没有及时收货订单");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SystemEmailType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SystemEmailType fromInt(int i) {
        switch (i) {
            case 1:
                return NOTRECEIVE;
            default:
                return NOTRECEIVE;
        }
    }
}
