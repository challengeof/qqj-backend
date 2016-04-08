package com.mishu.cgwy.order.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class OrderQueryRequest {
    private Date start;
    private Date  end;
    private Integer status;
    private Long restaurantId;
    private String restaurantName;
    private Long customerId;
    private Long adminId;
    private Long orderId;
    private Date expectedArrivedDate;

    private int page = 0;
    private int pageSize = 100;

    private String sortProperty = "id";
    private String sortDirection = "desc";

    private boolean PromotionTag = false;
    private Long warehouseId;
    private String vendorId;
    private Long cityId;
    private Long organizationId;
    private Long depotId;
    private Long blockId;

    private String sortField = "id";
    private boolean asc = false;

    private Integer coordinateLabeled;
    private boolean refundsIsNotEmpty = false;

    private Long spikeItemId;

    private String telephone;

    private Long orderType;
}

