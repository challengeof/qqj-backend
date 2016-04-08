package com.mishu.cgwy.order.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2015/12/10.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderType {

    NOMAL(1L,"普通"),GIFT(2L,"赠品"),OUTOFSTOCK(3L,"缺货补单"),APP_ORDER(4L,"App下单");

    private final Long val;
    private final String desc;

    OrderType(Long val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public Long getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderType find(Long type,OrderType defaultType) {

        for(OrderType otype : OrderType.values()){
            if(otype.val==type){
                return otype;
            }
        }
        return defaultType;
    }
}
