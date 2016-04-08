package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.stock.domain.SellReturnType;
import lombok.Data;

import java.util.Date;

/**
 * Created by wangwei on 15/10/19.
 */
@Data
public class SellReturnQueryRequest {

    private Long cityId;
    private Long organizationId;
    private Long depotId;
    private Long orderId;
    private Long restaurantId;
    private Long trackerId;
    private String restaurantName;
    private Long skuId;
    private String skuName;
    private Date startDate;
    private Date endDate;
    private Date startReturnDate;
    private Date endReturnDate;
    private Integer type;
    private Integer status;

    private Integer page = 0;
    private Integer pageSize = 100;
}
