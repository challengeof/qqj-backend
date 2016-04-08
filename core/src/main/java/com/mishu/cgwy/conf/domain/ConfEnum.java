package com.mishu.cgwy.conf.domain;

/**
 * Created by wangguodong on 15/12/23.
 */
public enum ConfEnum {
    ORDER_LIMIT("order_limit");

    private String name;

    public String getName() {
        return name;
    }

    private ConfEnum(String name) {
        this.name = name;
    }
}
