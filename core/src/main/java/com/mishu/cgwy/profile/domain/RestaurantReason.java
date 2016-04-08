package com.mishu.cgwy.profile.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangwei on 15/12/22.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantReason {

    SHUTDOWN(1, "商户关店"), EVIL(2, "恶意客户"), NOT_SERVICE(3, "非配送区域"), INFORMATION_UNUSED(4 ,"客户信息不准确"), AGENT(5, "经销商或代理商客户"), REPEATED(6, "重复客户"), TEST(7, "测试客户");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private RestaurantReason(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RestaurantReason fromInt(int i) {

        for (RestaurantReason rstatus : RestaurantReason.values()) {
            if (rstatus.getValue() == i) {
                return rstatus;
            }
        }
        return SHUTDOWN;
    }

}
