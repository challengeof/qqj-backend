package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StockTotalDailyRequest {
    private Long cityId;
    private Long skuId;
    private String skuName;
    private Long categoryId;
    private Date startCreateDate;
    private Date endCreateDate;
    private List<Long> categoryIds;
    private int page = 0;
    private int pageSize = 100;
}
