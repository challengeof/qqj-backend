package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.wrapper.CouponStatisticsWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

/**
 * Created by king-ck on 2015/12/17.
 */
@Data
public class CouponStatisticsResponse extends QueryResponse<CouponStatisticsWrapper> {

    private CouponStatisticsWrapper lineTotal;

}
