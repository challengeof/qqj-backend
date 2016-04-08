package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bowen on 15/11/11.
 */
@Data
public class SimpleCouponWrapper {

    private Long id;

    private String name;

    private Date start;

    private Date end;

    private String description;

    private String remark;

    private BigDecimal discount = BigDecimal.ZERO;

    private SimpleSkuWrapper sku;

    private int quantity;

    private Date createTime;

    private Date deadline;

    private CouponConstant type;

    private Long score;


    public SimpleCouponWrapper() {

    }

    public SimpleCouponWrapper(Coupon coupon) {
        id = coupon.getId();
        name = coupon.getName();
        start = coupon.getStart();
        end = coupon.getEnd();
        description = coupon.getDescription();
        discount = coupon.getDiscount();
        remark = coupon.getRemark();
        if (coupon.getSku() != null) {

            sku = new SimpleSkuWrapper(coupon.getSku());
            quantity = coupon.getQuantity();
        }
        createTime = coupon.getCreateTime();
        deadline = coupon.getDeadline();
        this.type = CouponConstant.getCouponConstantByType(coupon.getCouponConstants());
        score = coupon.getScore();
    }
}
