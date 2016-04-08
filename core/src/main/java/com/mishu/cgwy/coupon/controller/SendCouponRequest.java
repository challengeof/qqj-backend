package com.mishu.cgwy.coupon.controller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SendCouponRequest {

    private Long couponId;

    private List<Long> restaurantIds;

    private Short reason;

    private String remark;
}
