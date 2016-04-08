package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/9/18.
 */
@Data
public class StockInItemData {
    private Long stockInItemId;
    private Long skuId;
    private String skuName;
    private String skuSingleUnit;
    private int expectedQuantity;
    private int realQuantity;
    private Date productionDate;
}
