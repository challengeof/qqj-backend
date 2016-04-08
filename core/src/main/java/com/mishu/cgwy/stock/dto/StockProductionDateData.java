package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by admin on 15/9/22.
 */
@Data
public class StockProductionDateData {

    private Long id;
    private int quantity;
    private List<StockProductionDateItemData> stockProductionDates;
}
