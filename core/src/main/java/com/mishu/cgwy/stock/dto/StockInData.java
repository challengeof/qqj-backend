package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/18.
 */
@Data
public class StockInData {

    private int stockInType;
    private Long stockInId;
    private boolean part = false;
    private List<StockInItemData> stockInItems = new ArrayList<>();
}
