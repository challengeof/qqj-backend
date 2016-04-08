package com.mishu.cgwy.coupon.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2015/12/14.
 */
@Data
public class CouponStatisticsRequest {

    private Long cityId;
    private Long warehouseId;

    private String restaurantName; //餐馆名称

    private Long restaurantId; // 餐馆id
    private Long orderId; //orderid

    private Integer couponType;
    private Integer couponStatus;

    private Date startFront; //统计时间
    private Date startBack;  //统计时间

    private Date sendFront;  //发送时间
    private Date sendBack;   //发送时间

    private Date useFront;   //使用时间
    private Date useBack;    //使用时间

    private Date orderDateFront; //下单日期
    private Date orderDateBack;

    private Date stockoutDateFront; //下单日期
    private Date stockoutDateBack;  //收货日期


    private Date endFront;   //优惠券过期截止时间
    private Date endBack;    //优惠券过期截止时间

    private Long couponIdFront; //couponId
    private Long couponIdBack;  // couponId

    private int page = 0;
    private int pageSize = 100;

    private String sortField = "id";
    private boolean asc = false;

    public CouponStatisticsRequest(){};

    public CouponStatisticsRequest(Long cityId, Long warehouseId, String restaurantName, Long restaurantId, Date startFront, Date startBack, Date useFront,Date useBack ,Integer couponStatus) {
        this.cityId = cityId;
        this.warehouseId = warehouseId;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.startFront = startFront;
        this.startBack = startBack;
        this.couponStatus = couponStatus;
    }
}
