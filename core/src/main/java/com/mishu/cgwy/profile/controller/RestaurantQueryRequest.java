package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.profile.constants.CustomerQueryType;
import com.mishu.cgwy.request.Request;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 11:23 AM
 */
@Data
public class RestaurantQueryRequest extends Request{
    protected Long blockId; // 板块
    protected Integer restaurantActiveType; //客户类型
    protected Integer cooperatingState;    //合作状态 正常，跟进，搁置
    protected String receiver;//联系人
    protected Integer restaurantType;

    private String name;
    private String telephone;
    private String registPhone;
    private Long zoneId;
    private Long adminUserId;
    private Long devUserId;
    private Integer status;

    private boolean adminUserIdIsNull = false;

    private int page = 0;
    private int pageSize = 100;

    private Date start;
    private Date end;
    private Date orderDate;
    private int blankTime = 0;

    private Long warehouseId;
    private Date createTime;
    private Long id;
    private Long cityId;
    private Long organizationId;

    private Short grade;
    private Integer warning;
    private String sortField = "id";
    private boolean asc = false;
    private boolean neverOrder = false;

    private String queryType= CustomerQueryType.all.val;





}
