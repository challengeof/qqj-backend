package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/2.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantActiveType {
    potential(1, "潜在客户"),//没有进行过审核的客户
    deal(2, "成交客户"),//通过审核的客户
    NotActive(3, "不活跃客户");//被销售人员确认2个月没成交订单的客户

    public final Integer val;
    public final String detail;

    private RestaurantActiveType(Integer val, String detail) {
        this.val = val;
        this.detail = detail;
    }

    public static RestaurantActiveType fromInt(Integer val , RestaurantActiveType defaultE){
        if(val==null){
            return defaultE;
        }
        for(RestaurantActiveType rcs : RestaurantActiveType.values()){
            if(rcs.val.intValue()==val){
                return rcs;
            }
        }
        return defaultE;
    }
    public static RestaurantActiveType fromInt(Integer val){
        return fromInt(val,null);
    }
}
