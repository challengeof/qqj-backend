package com.mishu.cgwy.promotion.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2015/12/23.
 */
@Data
public class PromotionStatisticsRequest {
    //活动类型   城市  市场  订单id  下单时间区间   活动id  skuid  sku名称 餐馆id  餐馆名称   导出excel
    private Integer promotionType;

    private Long cityId;

    private Long warehouseId;

    private Long orderId;

    private Date orderSubmitFront;

    private Date orderSubmitBack;

    private Date stokoutTimeFront;

    private Date stokoutTimeBak;

    private Long promotionId;

    private Long skuId;

    private String skuName;

    private Long restaurantId;

    private String restaurantName;

    private int page = 0;
    private int pageSize = 100;

    private String sortField = "id";
    private boolean asc = false;

}
