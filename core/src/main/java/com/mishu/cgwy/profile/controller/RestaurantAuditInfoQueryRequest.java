package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.profile.vo.RestaurantAuditReviewVo;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/3/10.
 */
@Data
public class RestaurantAuditInfoQueryRequest {

    private int page = 0;
    private int pageSize = 100;
    private String sortField = "id";
    private boolean asc = false;

    private Long restaurantId;
    private Integer restaurantStatus; //餐馆状态
    private Long operater; // 操作人


    private Integer status; // RestaurantReviewStatus  通过 驳回
    private Long createUser; // 创建人
    private Integer reqType; //申请审核类型  RestaurantAuditReviewType
    private Date createTime;

    private Date operateTimeFront;
    private Date operateTimeBack;

    private Date createTimeFront;
    private Date createTimeBack;

    private String restaurantName;
    private String createUserName;


}
