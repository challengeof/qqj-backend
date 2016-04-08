package com.mishu.cgwy.coupon.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mishu.cgwy.coupon.wrapper.PromotionConstantWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/10/27.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PromotionConstant {
    TWO_FOR_ONE(3, true,"买一赠一"),
    ORDER_WITH_A_GIFT_SEND(2, true,"满赠活动"),
    FULL_MINUS(1, false,"满减活动");

    private Integer type;

    private String name;

    private boolean isGoods; //是否是赠送物品

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isGoods() {
        return isGoods;
    }

    private PromotionConstant(Integer type, boolean isGoods,String name) {
        this.type = type;
        this.name = name;
        this.isGoods=isGoods;
    }

    public static PromotionConstant getPromotionConstantByType(Integer type) {
        for (PromotionConstant promotionConstant : PromotionConstant.values()) {
            if (promotionConstant.type.equals(type)) {
                return promotionConstant;
            }
        }

        return null;
    }

    public static List<PromotionConstantWrapper> getPromotionConstants() {
        List<PromotionConstantWrapper> list = new ArrayList<>();
        for (PromotionConstant promotionConstant : PromotionConstant.values()) {
            PromotionConstantWrapper promotionConstantWrapper = new PromotionConstantWrapper(promotionConstant);

            list.add(promotionConstantWrapper);
        }
        return list;
    }
}


