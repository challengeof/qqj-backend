package com.mishu.cgwy.coupon.wrapper;

import com.mishu.cgwy.coupon.constant.PromotionConstant;
import lombok.Data;

/**
 * Created by bowen on 15/10/27.
 */
@Data
public class PromotionConstantWrapper {

    private Integer type;

    private String name;

    private boolean isGoods;

    public PromotionConstantWrapper() {
    }

    public PromotionConstantWrapper(PromotionConstant promotionConstant) {
        this.type = promotionConstant.getType();
        this.name = promotionConstant.getName();
        this.isGoods= promotionConstant.isGoods();
    }
}
