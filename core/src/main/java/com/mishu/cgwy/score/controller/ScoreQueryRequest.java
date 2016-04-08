package com.mishu.cgwy.score.controller;

import lombok.Data;

/**
 * Created by king-ck on 2015/11/10.
 */
@Data
public class ScoreQueryRequest {

    private Long cityId;
    private String restaurantName;
    private Long warehouseId; //市场id
    private Long restaurantId; // 餐馆id

    private Long adminUserId;

    private Integer status; //状态
    private Short grade;  //客户等级

    private int page = 0;
    private int pageSize = 100;


    private String sortField = "id";
    private boolean asc = false;


}
