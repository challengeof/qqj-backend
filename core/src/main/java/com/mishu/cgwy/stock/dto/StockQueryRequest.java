package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:20 PM
 */
@Data
public class StockQueryRequest {

    private Long cityId;

    private Long depotId;

    private Long skuId;

    private String skuName;

    private BigDecimal taxRate;

    private Integer status;//0:可用;1:占用;2:在途

    private Long categoryId;

    private List<Long> categoryIds;

    private Boolean shelfIsNull;

    private String shelfCode;

    private Date expirationDate;

    private String productionDate;

    private Integer expireDays;

    private Integer dullSaleDays;

    private int page = 0;

    private int pageSize = 100;

    private String sortField;

    private boolean asc = false;

}
