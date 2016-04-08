package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StockProductionDateItemData {

    private Date productionDate;
    private int quantity;
}
