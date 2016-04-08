package com.mishu.cgwy.coupon.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by king-ck on 2015/12/16.
 */
@Data
public class CouponStatisticsUsedDetailWrapper {

    private Date useDate;
    private Date submitOrderDate; // 下单时间
    private Date stockOutDate;  //收货时间

    private Long orderId;

    private Long restaurantId; // 餐馆id
    private String restaurantName; //餐馆名称
    private Long couponId;
    private String couponName;
    private Integer couponType;
    private String couponTypeDesc;

    private BigDecimal discount;       //优惠券金额
    private BigDecimal orderSubTotal; //订单金额
    private String remark;             //备注

    public CouponStatisticsUsedDetailWrapper(Date useDate, Date submitOrderDate, Date stockOutDate, Long orderId, Long restaurantId, String restaurantName, Long couponId,
                                             String couponName,Integer couponType, BigDecimal discount, BigDecimal orderSubTotal, String remark) {
        this.useDate = useDate;
        this.submitOrderDate = submitOrderDate;
        this.stockOutDate = stockOutDate;

        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponType = couponType;
        this.discount = discount;
        this.orderSubTotal = orderSubTotal;
        this.remark = remark;

        this.couponTypeDesc= CouponConstant.getCouponConstantByType(couponType).getName();

    }
}
