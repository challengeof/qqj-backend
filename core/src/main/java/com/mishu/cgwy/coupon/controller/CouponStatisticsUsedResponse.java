package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedTotalWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2015/12/16.
 */
@Data
public class CouponStatisticsUsedResponse extends QueryResponse<CouponStatisticsUsedWrapper> {

    private CouponStatisticsUsedTotalWrapper lineTotal;


}
