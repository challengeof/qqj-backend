package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 餐馆对应销售人员的类型
 * Created by king-ck on 2016/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantSellerType {

    keep(2, "维护"), dev(1,"开发");

    public  final Integer val;
    public  final String detail;

    private RestaurantSellerType(Integer val, String detail) {
        this.val = val;
        this.detail = detail;
    }

    public static RestaurantSellerType fromInt(Integer val) {
        for (RestaurantSellerType type : RestaurantSellerType.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return keep;
    }

}
