package com.mishu.cgwy.stock.dto;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class StockTotalRequest {
    private Long cityId;
    private Long skuId;
    private String skuName;
    private Long categoryId;
    private int page = 0;
    private int pageSize = 100;
}
