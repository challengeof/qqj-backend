package com.mishu.cgwy.saleVisit.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/8/13.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SaleVisitSolution {

    OTHER(0, "其他"),
    TRANSFER_CUSTOMER_FOCUS_TO_QUOTATION(1, "建议看整体报价,转移客户关注点"),//Advice to see the overall quotation, transfer customer focus
    CONFIRM_QUANTITY_RETURN_PROCESSING(2, "确认质量,退换货处理"),//Confirm the quality, return processing
    NEGOTIATE_RECEIVING_TIME(3, "与客户协商收货时间"),//Negotiate with customer receiving time
    APPEASE_CUSTOMER_UNDERSTAND_EVENT(4, "安抚客户,了解具体事件"),//To appease the customer, understand the specific events
    UNDERSTAND_COMPETITOR(5, "了解竞争对手");//Understand competitor

    private Integer value;
    private String name;

    SaleVisitSolution(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static SaleVisitSolution fromInt(int value) {
        for (int i = 0; i < values().length; i++) {
            if (value == values()[i].getValue()) {
                return values()[i];
            }
        }
        return OTHER;
    }
}
