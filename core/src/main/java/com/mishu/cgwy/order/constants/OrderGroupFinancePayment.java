package com.mishu.cgwy.order.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 15/9/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderGroupFinancePayment {

    WEIXIN(1,"微信支付"), ALIPAY(2,"支付宝支付"),CASH(3,"现金支付");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private OrderGroupFinancePayment(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static OrderGroupFinancePayment fromInt(int i) {
        switch (i) {
            case 1:
                return WEIXIN;
            case 2:
                return ALIPAY;
            case 3:
                return CASH;
            default:
                return null;
        }
    }

}
