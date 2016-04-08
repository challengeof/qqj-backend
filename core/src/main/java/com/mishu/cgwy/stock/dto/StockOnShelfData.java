package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 15/9/22.
 */
@Data
public class StockOnShelfData {

    private Long id;
    private Long depotId;
    private Long skuId;
    private Integer availableQuantity;
    private Date expirationDate;
    private List<StockOnShelfItemData> stockShelfs;
    private Long shelfId;
    private String shelfCode;
    private int quantity;
    private int moveQuantity;

}
