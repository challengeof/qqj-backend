package com.qqj.product.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProductStatus {
    INVALID((short)0, "无效"),
    VALID((short)1, "正常");

    private Short value;
    private String name;

    public Short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private ProductStatus(Short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ProductStatus get(Short value) {
        for (ProductStatus i : ProductStatus.values()) {
            if (i.value.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
