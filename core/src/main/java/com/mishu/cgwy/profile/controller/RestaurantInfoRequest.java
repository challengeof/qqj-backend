package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/3/1.
 */
@Data
public class RestaurantInfoRequest extends RestaurantInfoVo{

    private Date registDateFront;
    private Date registDateBack;

    private int page = 0;
    private int pageSize = 100;
    private String sortField = "id";
    private boolean asc = false;

    private Integer customerActiveType; // 潜在客户，成交客户， 不活跃客户

    private String addType;  // RestaurantAddType

}
