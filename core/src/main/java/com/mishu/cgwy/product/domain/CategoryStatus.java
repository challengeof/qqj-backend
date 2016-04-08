package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: xudong
 * Date: 3/18/15
 * Time: 10:24 PM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CategoryStatus {

    UNDEFINED(1, "未审核"), ACTIVE(2, "生效"), INACTIVE(3, "失效");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CategoryStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CategoryStatus fromInt(int i) {
        switch (i) {
            case 1:
                return UNDEFINED;
            case 2:
                return ACTIVE;
            case 3:
                return INACTIVE;
            default:
                return UNDEFINED;
        }
    }

    @JsonCreator
    public static CategoryStatus fromObject(final JsonNode jsonNode) {
        return fromInt(jsonNode.get("value").asInt());
    }
}
