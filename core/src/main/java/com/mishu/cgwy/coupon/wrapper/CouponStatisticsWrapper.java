package com.mishu.cgwy.coupon.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by king-ck on 2015/12/15.
 */
@Data
public class CouponStatisticsWrapper {

    private Long customerCouponId;

    private Long restaurantId; // 餐馆id
    private String restaurantName; //餐馆名称
    private Integer couponType;
    private Integer couponStatus;
    private String couponStatusDesc;
    private String couponTypeDesc;

    private Long couponId;
    private String couponName;
    private Date sendTime;
    private Date endTime;
    private BigDecimal discount;
    private String remark;

    private String sender;

    private String operater;
    private Date operateTime;


    public CouponStatisticsWrapper( BigDecimal discount) {
        this.discount=discount;
    }

    public CouponStatisticsWrapper(Long restaurantId, String restaurantName, Integer couponType, Integer couponStatus,
                                   Long couponId, String couponName, Date sendTime, Date endTime, BigDecimal discount,
                                   String remark, String sender, String operater, Date operateTime, Long customerCouponId) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.couponType = couponType;
        this.couponStatus = couponStatus;

        this.couponTypeDesc = CouponConstant.getCouponConstantByType(couponType).getName();
        this.couponStatusDesc = CouponStatus.fromInt(couponStatus).getName();

        this.couponId = couponId;
        this.couponName = couponName;
        this.sendTime = sendTime;
        this.endTime = endTime;
        this.discount = discount;
        this.remark = remark;
        this.sender = sender;

        this.operater=operater;
        this.operateTime=operateTime;

        this.customerCouponId=customerCouponId;
    }
}
