package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedDetailWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by king-ck on 2015/12/17.
 */
@Data
public class CouponStatisticsUsedDetailResponse extends QueryResponse<CouponStatisticsUsedDetailWrapper> {

    private BigDecimal lineTotal;
}
