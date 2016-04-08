package com.mishu.cgwy.saleVisit.request;

import lombok.Data;

import java.util.Date;

/**
 * Created by wangwei on 15/8/13.
 */
@Data
public class SaleVisitQueryRequest {

    private Long restaurantId;
    private String restaurantName;
    private String sellerName;

    private Integer activeType;
    private Long visitId;


    private Integer visitStage;
    private Integer visitPurpose;

    private Date startVisitTime;
    private Date endVisitTime;

    private int page = 0;
    private int pageSize = 15;

}
