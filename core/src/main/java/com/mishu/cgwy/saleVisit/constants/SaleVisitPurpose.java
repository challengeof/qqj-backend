package com.mishu.cgwy.saleVisit.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/8/13.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SaleVisitPurpose {

    UNDERSTAND_CUSTOMER_NEEDS(0, "了解客户需求"),//Understand customer needs
    UNDERSTAND_ORIGINAL_SUPPLIER_WORK_ITEM_PRICE(1, "了解原供货商、合作品项、价格"),//To know the original supplier work item price
    UNDERSTAND_INVENTORY(2, "了解库存"),//Understand the inventory
    CONTRACT_SIGNING(3, "合同签订"),//CONTRACT SIGNING
    DEAL_WITH_COMPLAINTS_AND_SUGGESTIONS(4, "处理投诉和建议"),//Deal with complaints and Suggestions
    CHECKING_ACCOUNT(5, "对账"),//account checking
    SEND_INVOICE(6, "送发票"),//Send the invoice
    REQUESTING_PAYMENT(7, "催款"),//Pressing for Payment//Requesting a Payment
    MAKE_COLLECTIONS(8, "收款"),//make collections
    ASSIST_LOGISTICS_DELIVERY(9, "协助物流送货"),//Assist logistics delivery
    SUGGEST_CUSTOMER_ORDER(10, "建议客户订货"),// suggest  customer to order
    HAND_OUT_SAMPLES(11, "赠送样品");//Hand out samples

    private Integer value;
    private String name;

    SaleVisitPurpose(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static SaleVisitPurpose fromInt(int value) {
        for (int i = 0; i < values().length; i++) {
            if (value == values()[i].getValue()) {
                return values()[i];
            }
        }
        return UNDERSTAND_CUSTOMER_NEEDS;
    }
}
