package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangwei on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SkuBundleUnit {

    box("箱"),piece("件"),cover("套"), dozen("打"), roll("卷");

    private String name;

    public String getName() {
        return name;
    }

    private SkuBundleUnit(String name) {
        this.name = name;
    }
}
