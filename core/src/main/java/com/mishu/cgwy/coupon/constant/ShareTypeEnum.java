package com.mishu.cgwy.coupon.constant;

/**
 * Created by king-ck on 2015/11/19.
 */
public enum ShareTypeEnum {

    coupon(null,"coupon","优惠券相关分享"),
    scoreShareRegister(1,"score","积分分享注册");

    public final Integer val;
    public final String alias;
    public final String desc;

    ShareTypeEnum(Integer val, String alias, String desc) {
        this.val = val;
        this.alias=alias;
        this.desc = desc;
    }

    public static ShareTypeEnum findShareType(String alias){
        for (ShareTypeEnum ste : ShareTypeEnum.values()) {
           if(ste.alias.equals(alias)){
                return ste;
           }
        }
        return null;
    }

    public static ShareTypeEnum findShareType(Integer val){
        for (ShareTypeEnum ste : ShareTypeEnum.values()) {
            if (ste.val == val) {
                return ste;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ShareTypeEnum{" +
                "val=" + val +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
