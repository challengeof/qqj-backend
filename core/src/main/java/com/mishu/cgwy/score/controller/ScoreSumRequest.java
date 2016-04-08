package com.mishu.cgwy.score.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2015/11/16.
 */
@Data
public class ScoreSumRequest {

    private Long customerId;
    private Long restaurantId;
    private Integer scoreLogStatus;
    private Date orderBeginDate;  //订单时间区间-起始时间
    private Date orderEndDate;   //订单时间区间-截止时间

}
