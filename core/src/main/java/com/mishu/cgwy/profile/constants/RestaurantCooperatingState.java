package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 合作状态 字段枚举
 * Created by king-ck on 2016/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantCooperatingState {

//    合作状态 正常，跟进，搁置

    nomal(1, "正常"), follow(2, "跟进"), shelve(3, "搁置");

    public final Integer val;
    public final String detail;

    private RestaurantCooperatingState(Integer val, String detail) {
        this.val = val;
        this.detail = detail;
    }

    public static RestaurantCooperatingState fromInt(Integer val , RestaurantCooperatingState defaultE){
        if(val==null){
            return defaultE;
        }
        for(RestaurantCooperatingState rcs : RestaurantCooperatingState.values()){
            if(rcs.val.intValue()==val){
                return rcs;
            }
        }
        return defaultE;
    }
    public static RestaurantCooperatingState fromInt(Integer val){
        return fromInt(val,null);
    }


}
