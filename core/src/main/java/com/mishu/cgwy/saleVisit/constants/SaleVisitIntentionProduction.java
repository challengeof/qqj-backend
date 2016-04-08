package com.mishu.cgwy.saleVisit.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/8/13.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SaleVisitIntentionProduction {

    TIME_LIMIT_SHOPPING(0,"限时抢购"),
    WY_PRIVILEGE(1,"无忧特价"),
    CONDIMENT(2,"调味品"),
    DRY_MERCHANDISE(3,"南北干货"),
    FROZEN_MERCHANDISE(4,"冻货"),
    RICE_NOODLE_GRAIN_OIL(5,"米面粮油"),
    WINES_DRINKS(6,"酒水饮料"),
    KITCHENWARE(7,"餐厨用品"),
    GREEN_GOODS(8,"新鲜蔬菜"),
    HOT_SELL(9,"爆款热卖");

    private Integer value;
    private String name;

    SaleVisitIntentionProduction(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static SaleVisitIntentionProduction fromInt(int value) {
        for (int i = 0; i < values().length; i++) {
            if (value == values()[i].getValue()) {
                return values()[i];
            }
        }
        return CONDIMENT;
    }
}
