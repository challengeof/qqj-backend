package com.mishu.cgwy.organization.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/7/2.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrganizationStatus {

    UNDEFINED(1, "未审核"), ACTIVE(2, "生效"), INACTIVE(3, "失效");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private OrganizationStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static OrganizationStatus fromInt(Integer value) {
        if(value == null) {
            value = 1;
        }
        for(int i = 0; i < values().length; i ++) {
            if(values()[i].getValue().equals(value)){
                return values()[i];
            }
        }
        return UNDEFINED;
    }

}
