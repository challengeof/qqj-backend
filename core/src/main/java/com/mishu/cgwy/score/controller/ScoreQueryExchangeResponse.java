package com.mishu.cgwy.score.controller;

import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.score.Wrapper.ScoreResponse;
import com.mishu.cgwy.score.Wrapper.ScoreWrapper;
import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2016/1/19.
 */
@Data
public class ScoreQueryExchangeResponse {

    private CustomerWrapper customer;
    private ScoreWrapper score;
    private List<SimpleCouponWrapper> coupons;

    public ScoreQueryExchangeResponse(ScoreWrapper score, List<SimpleCouponWrapper> coupons, CustomerWrapper customer) {
        this.score=score;
        this.coupons = coupons;
        this.customer = customer;
    }
}
