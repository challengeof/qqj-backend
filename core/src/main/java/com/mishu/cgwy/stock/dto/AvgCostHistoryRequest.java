package com.mishu.cgwy.stock.dto;

import lombok.Data;

/**
 * Created by admin on 18/9/16.
 */
@Data
public class AvgCostHistoryRequest {
    private Long cityId;
    private Long skuId;
    private String skuName;
    private int page = 0;
    private int pageSize = 100;
}
