package com.mishu.cgwy.coupon.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by challenge on 15-6-29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CouponStatus {

    UNUSED(1, "未使用"), USED(2, "已使用"), EXPIRED(3, "已过期"), CANCELLED(4,"已失效"), INVALID(5, "退货作废");


    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private CouponStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CouponStatus fromInt(int i) {
        for( CouponStatus cStatus : CouponStatus.values()){
            if(cStatus.getValue()==i){
                return cStatus;
            }
        }
        return CouponStatus.UNUSED;
    }

    @JsonCreator
    public static CouponStatus fromObject(final JsonNode jsonNode) {
        return fromInt(jsonNode.get("value").asInt());
    }

}
