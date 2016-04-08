package com.mishu.cgwy.score.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2015/11/10.
 */
@Data
public class ScoreLogQueryRequest {

    private Long cityId;
    private String restaurantName;
    private Long warehouseId; //市场id
    private Long restaurantId; // 餐馆id
    private Long customerId;

    private Long adminUserId;

    private Integer status; //商户状态
    private Short grade;  //客户等级

    private Date orderBeginDate;  //订单时间区间-起始时间
    private Date orderEndDate;   //订单时间区间-截止时间

    private Integer scoreLogStatus;  //积分类型

    private int page = 0;
    private int pageSize = 100;


    private String sortField = "id";
    private boolean asc = false;

}
