package com.mishu.cgwy.coupon.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class CouponConstantWrapper {
    private Integer type;

    private String name;

    public CouponConstantWrapper() {
    }

    public CouponConstantWrapper(CouponConstant couponConstant) {
        this.type = couponConstant.getType();
        this.name = couponConstant.getName();
    }
}
