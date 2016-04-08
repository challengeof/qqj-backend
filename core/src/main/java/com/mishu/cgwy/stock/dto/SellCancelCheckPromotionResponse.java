package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.order.wrapper.CustomerCouponWrapper;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 16/1/13.
 */
@Data
public class SellCancelCheckPromotionResponse {

    private List<CustomerCouponWrapper> customerCoupons = new ArrayList<>();

    private List<PromotionWrapper> promotions = new ArrayList<>();
}
