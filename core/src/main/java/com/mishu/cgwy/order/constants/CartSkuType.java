package com.mishu.cgwy.order.constants;

/**
 * Created by king-ck on 2016/1/12.
 */
public enum CartSkuType {
    normal(1,"普通"), spike(2,"秒杀商品");

    public final int val;
    public final String desc;

    CartSkuType(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static CartSkuType fromInt(int val){
        for(CartSkuType cstype : CartSkuType.values()){
            if(cstype.val==val){
                return cstype;
            }
        }
        return  null;
    }
}
