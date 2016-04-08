package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/7.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantReviewStatus {

    NOT_CHECK(1,"审核中"), PASS(2,"通过"),FAIL(3,"驳回");
    public final Integer val;
    public final String msg ;

    RestaurantReviewStatus(Integer val, String msg) {
        this.val = val;
        this.msg = msg;
    }

    public static RestaurantReviewStatus fromInt(Integer val) {
        for (RestaurantReviewStatus type : RestaurantReviewStatus.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return null;
    }
}
