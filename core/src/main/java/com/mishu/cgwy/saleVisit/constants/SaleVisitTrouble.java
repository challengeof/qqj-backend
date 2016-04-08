package com.mishu.cgwy.saleVisit.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by apple on 15/8/13.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SaleVisitTrouble {

    OTHER(0, "其他"),
    HIGH_PRICE(1, "价格偏高"),
    QUANTITY_PROBLEM(2, "质量问题"),
    LOGISTICS_DELAY(3, "物流不及时"),
    LOGISTICS_POOR_ATTITUDE(4, "物流态度差"),
    PRODUCTION_SEND_ERROR(5, "货物错发漏发"),
    COMPETITOR_INFLUENCE(6, "竞争对手影响");

    private Integer value;
    private String name;

    SaleVisitTrouble(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static SaleVisitTrouble fromInt(int value) {
        for (int i = 0; i < values().length; i++) {
            if (value == values()[i].getValue()) {
                return values()[i];
            }
        }
        return OTHER;
    }
}
