package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by xingdong on 15/7/8.
 */
@Data
public class OrderGroupQueryRequest {
    private Integer queryDateType; //1:查询订单包本身createDate | 其它:查询订单包关联订单时间
    private Date startOrderDate;
    private Date endOrderDate;
    private Long depotId;
    private Long cityId;
    private Long trackerId;

    @Deprecated
    private Long warehouseId;
    @Deprecated
    private Date expectedArrivedDate;
    @Deprecated
    private Long organizationId;
    @Deprecated
    private Long skuId;

    private int page;
    private int pageSize = 100;

}
