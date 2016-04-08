package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
public class StockAdjustData {

    private Long stockId;
    private int quantity;
    private int adjustQuantity;
    private String comment;
    private Set<Long> adjustIds;

    private Long depotId;
    private Long skuId;
    private BigDecimal avgCost;
    private Date productionDate;
    private BigDecimal taxRate;

}
