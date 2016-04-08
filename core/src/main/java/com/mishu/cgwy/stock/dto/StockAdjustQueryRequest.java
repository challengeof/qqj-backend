package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StockAdjustQueryRequest {

    private Long cityId;

    private Long depotId;

    private Long skuId;

    private String skuName;

    private Integer status;//0:待审核;1:已通过;2:已拒绝

    private Long categoryId;

    private List<Long> categoryIds;

    private Date startCreateDate;
    private Date endCreateDate;
    private Date startAuditDate;
    private Date endAuditDate;

    private int page = 0;

    private int pageSize = 100;
}
