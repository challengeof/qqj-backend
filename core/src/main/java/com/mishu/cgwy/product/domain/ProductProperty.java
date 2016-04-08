package com.mishu.cgwy.product.domain;

import lombok.Data;

/**
 * User: xudong
 * Date: 5/7/15
 * Time: 4:20 PM
 */
@Data
public class ProductProperty {
    private String name;
    private String displayName;
    private String value;

    public ProductProperty() {

    }

    public ProductProperty(String name, String displayName, String value) {
        this.name = name;
        this.displayName = displayName;
        this.value = value;
    }


}
