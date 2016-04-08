package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/18.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantAddType {
    sea("seaAdd", "通过客户公海增加"),
    my("myAdd", "通过我的客户增加");

    public final String val;
    public final String desc;

    private RestaurantAddType(String val, String detail) {
        this.val = val;
        this.desc = detail;
    }


    public static RestaurantAddType find(String val) {
        for (RestaurantAddType type : RestaurantAddType.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return null;
    }
}
