package com.mishu.cgwy.workTicket.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 16/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProblemSources {

    WECHAT_GROUP(1, "微信群"),
    WECHAR_PLATFORM(2, "微信平台"),
    BACKGROUND(3, "后台系统"),
    CUSTOMER_SERVICES_TELEPHONE(4, "400-客服电话"),
    APP(5, "app"),
    OTHER(6, "其他");


    private Integer type;

    private String name;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private ProblemSources(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

}

