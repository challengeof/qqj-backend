package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantSellerLogType {

    allot(1, "分配"), claim(2, "认领");

    public final Integer val;
    public final String detail;

    private RestaurantSellerLogType(Integer val, String detail) {
        this.val = val;
        this.detail = detail;
    }
}
