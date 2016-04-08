package com.mishu.cgwy.vendor.controller;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class VendorOrderHistoryListRequest {

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date start;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date end;

    private int page = 0;

    private int pageSize = 50;
}
