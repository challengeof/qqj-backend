package com.mishu.cgwy.profile.controller;

import lombok.Data;

/**
 * Created by king-ck on 2016/3/11.
 */
@Data
public class RestaurantAuditRequest {

    protected  Long[] auditReviewId;

    protected  Integer reviewStatus;

    protected  Integer reviewType;


    protected  Integer restaurantStatus;
    protected  Integer restaurantReason;
}
