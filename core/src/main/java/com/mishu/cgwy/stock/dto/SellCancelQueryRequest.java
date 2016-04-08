package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by wangwei on 15/10/19.
 */
@Data
public class SellCancelQueryRequest {

    private Long cityId;
    private Long organizationId;
    private Long depotId;
    private Long trackerId;
    private Long orderId;
    private Long restaurantId;
    private String restaurantName;
    private Long skuId;
    private String skuName;
    private Date startDate;
    private Date endDate;
    private Date startCancelDate;
    private Date endCancelDate;
    private Integer type;

    private Integer page = 0;
    private Integer pageSize = 100;
}
