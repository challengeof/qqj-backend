package com.mishu.cgwy.workTicket.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 16/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Process {

    CONSULTATION_PROCESS(1, "咨询流程"),
    NEW_PRODUCT_PROCESS(2, "新品处理流程"),
    RETURN_PROCESS(3, "退换货流程"),
    COMPLAINTS_PROCESS(4, "投诉流程"),
    ORDER_REPEAT_PROCESS(5, "订单查重流程"),
    NOT_DISTRIBUTION_CAR_ORDER_PROCESS(6, "未分车订单处理流程"),
    ORDER_EVALUATE_RETURN_VISIT_PROCESS(7, "评价回访流程"),
    FIRST_ORDER_RETURN_VISIT_PROCESS(8, "首单回访流程");

    private Integer type;

    private String name;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private Process(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
