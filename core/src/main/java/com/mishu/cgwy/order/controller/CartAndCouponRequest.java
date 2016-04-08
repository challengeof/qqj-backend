package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-6-30.
 */
@Data
public class CartAndCouponRequest {

    private List<CartRequest> cartRequestList = new ArrayList<>();

    private Long couponId;

    private String deviceId;

}
