package com.mishu.cgwy.purchase.controller;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ReturnNoteRequest {

    private Long cityId;

    private Long organizationId;

    private Long vendorId;

    private Long depotId;

    private String productName;

    private Long purchaseOrderId;

    private Short status;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
