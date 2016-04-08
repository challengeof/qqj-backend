package com.mishu.cgwy.saleVisit.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/8/13.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SaleVisitStage {

    DEVELOPING_INITIAL_VISIT(0, "开发中-初次拜访"),
    DEVELOPING_REPEAT_VISIT(1, "开发中-回访客户"),
    DEVELOPING_PRODUCTION_TRIAL(2, "开发中-样品试用"),
    DEVELOPING_QUOTE_MAKE(3, "开发中-报价"),
    DEVELOPING_CONTRACT_SIGN(4, "开发中-合同签订"),
    COOPERATION_CUSTOMER_MAINTAIN(5, "已合作-客情维护"),
    COOPERATION_PRODUCTION_ADD(6, "已合作-增加品项"),
    COOPERATION_PRODUCTION_TROUBLE(7, "已合作-产品问题处理"),
    COOPERATION_LOGISTICS_TROUBLE(8, "已合作-物流问题处理");

    private Integer value;
    private String name;

    SaleVisitStage(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static SaleVisitStage fromInt(int value) {
        for (int i = 0; i < values().length; i++) {
            if (value == values()[i].getValue()) {
                return values()[i];
            }
        }
        return DEVELOPING_INITIAL_VISIT;
    }
}
