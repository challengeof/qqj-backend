package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.wrapper.CouponStatisticsWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by king-ck on 2015/12/15.
 */
@Data
public class CouponSendDetailResponse extends QueryResponse<CouponStatisticsWrapper> {

    private BigDecimal discountSum; //金额合计

}
