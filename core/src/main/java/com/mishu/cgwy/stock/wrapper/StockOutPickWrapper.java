package com.mishu.cgwy.stock.wrapper;

import lombok.Data;

@Data
public class StockOutPickWrapper {

    private Long skuId;
    private String skuName;
    private String specification;
    private String skuSingleUnit;
    private String skuBundleUnitDes;
    private int expectedQuantity;
    private String shelfName;
    private String trackerName;
}
