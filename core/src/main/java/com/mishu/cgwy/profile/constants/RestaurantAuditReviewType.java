package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/10.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantAuditReviewType {

    claim(1, "认领审核"),
    restaurantInfo(2, "餐馆审核"),
    seaBack(3, "投放公海审核"),
    allot(4,"分配");

    public final Integer val;
    public final String desc;

    private RestaurantAuditReviewType(Integer val, String detail) {
        this.val = val;
        this.desc = detail;
    }


    public static RestaurantAuditReviewType fromInt(Integer val) {
        for (RestaurantAuditReviewType type : RestaurantAuditReviewType.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return null;
    }

}
