package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Created by wangwei on 15/9/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SkuSingleUnit {

    piece("件"),piecebottle("瓶"),bag("袋"),pot("罐"),box("盒"),pack("包"),jin("斤");

    private String name;

    public String getName() {
        return name;
    }

    private SkuSingleUnit(String name) {
        this.name = name;
    }
}
