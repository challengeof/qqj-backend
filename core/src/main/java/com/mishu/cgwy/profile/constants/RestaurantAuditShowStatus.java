package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/17.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantAuditShowStatus {

    claim_NOT_CHECK(0, RestaurantAuditReviewType.claim,RestaurantReviewStatus.NOT_CHECK,"认领审核中"),
    claim_PASS(1, RestaurantAuditReviewType.claim,RestaurantReviewStatus.PASS, "已认领" ),
    claim_fail(2, RestaurantAuditReviewType.claim,RestaurantReviewStatus.FAIL, "认领驳回" ),
    seaBack_NOT_CHECK(3,RestaurantAuditReviewType.seaBack,RestaurantReviewStatus.NOT_CHECK, "投放公海中"),
    seaBack_pass(4, RestaurantAuditReviewType.seaBack,RestaurantReviewStatus.PASS, "已投放公海"),
    seaBack_fail(5, RestaurantAuditReviewType.seaBack,RestaurantReviewStatus.FAIL, "投放公海驳回"),
    allocated(6, RestaurantAuditReviewType.allot,RestaurantReviewStatus.PASS, "已分配"),
    restaurant_NOT_CHECK(7,RestaurantAuditReviewType.restaurantInfo,RestaurantReviewStatus.NOT_CHECK, "餐馆审核中"),
    restaurant_CHECK_End(8,RestaurantAuditReviewType.restaurantInfo,RestaurantReviewStatus.PASS, "餐馆审核结束");

    public final Integer val;
    public final RestaurantAuditReviewType reviewType;
    public final RestaurantReviewStatus reviewStatus;
    public final String detail;

    private RestaurantAuditShowStatus(Integer val, RestaurantAuditReviewType reviewType, RestaurantReviewStatus reviewStatus, String detail) {
        this.val = val;
        this.reviewType = reviewType;
        this.reviewStatus = reviewStatus;
        this.detail = detail;
    }

    public static RestaurantAuditShowStatus find( Integer val ){
        for(RestaurantAuditShowStatus showStatus : RestaurantAuditShowStatus.values()){
            if(showStatus.val == val){
                return showStatus;
            }
        }
        return null;
    }

    public static RestaurantAuditShowStatus find( RestaurantAuditReviewType reviewType,  RestaurantReviewStatus reviewStatus ){
        for(RestaurantAuditShowStatus showStatus : RestaurantAuditShowStatus.values()){
            if(showStatus.reviewStatus == reviewStatus && showStatus.reviewType == reviewType){
                return showStatus;
            }
        }
        return null;
    }

}
