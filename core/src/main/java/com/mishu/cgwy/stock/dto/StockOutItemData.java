package com.mishu.cgwy.stock.dto;

import lombok.Data;

/**
 * Created by admin on 15/9/22.
 */
@Data
public class StockOutItemData {
    private Long stockOutItemId;
    private Long skuId;
    private String skuName;
    private boolean bundle;
    private String skuSingleUnit;
    private int expectedQuantity;
    private int realQuantity;
//    private int receiveQuantity;
    private int returnQuantity;
    private Long sellReturnReasonId;
}
