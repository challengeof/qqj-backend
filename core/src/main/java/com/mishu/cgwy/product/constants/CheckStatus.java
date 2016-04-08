package com.mishu.cgwy.product.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 15-6-5.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CheckStatus {
    NOT_AUDIT(0,"未审核"), REFUSED(1,"拒绝"),THROUGH(2,"通过");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CheckStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CheckStatus fromLong(int i) {
        switch (i) {
            case 0:
                return NOT_AUDIT;
            case 1:
                return REFUSED;
            case 2:
                return THROUGH;
            default:
                return NOT_AUDIT;
        }
    }

}
