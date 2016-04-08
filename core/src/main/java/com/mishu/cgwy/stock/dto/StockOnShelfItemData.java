package com.mishu.cgwy.stock.dto;

import lombok.Data;

@Data
public class StockOnShelfItemData {

    private Long shelfId;
    private String shelfCode;
    private int quantity;
}
