package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangwei on 16/1/15.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SkuPriceType {

    SALE_PRICE_LIMIT(1, "销售限价"), FIXED_PRICE(2, "销售定价"), SALE_PRICE(3, "商品售价"), PURCHASE_PRICE(4, "采购价");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SkuPriceType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SkuPriceType fromInt(int i) {
        for (SkuPriceType historyType : SkuPriceType.values()) {
            if (historyType.getValue().equals(i)) {
                return historyType;
            }
        }
        return null;
    }

}
