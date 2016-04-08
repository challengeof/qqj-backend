package com.mishu.cgwy.coupon.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CouponListRequest {
    private Integer couponType;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private int page = 0;

    private int pageSize = 100;
}
