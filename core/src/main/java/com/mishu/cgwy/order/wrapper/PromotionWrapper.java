package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.promotion.domain.Promotion;
import lombok.Data;

import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 4/19/15
 * Time: 10:22 PM
 */
@Data
public class PromotionWrapper {
    private Long id;

    private String description;

    private BigDecimal discount = BigDecimal.ZERO;

    private SimpleSkuWrapper sku;

    private int quantity;

    private boolean bundle;

    public PromotionWrapper() {

    }

    public PromotionWrapper(Promotion promotion) {
        id = promotion.getId();
        if (promotion.getPromotableItems() != null && promotion.getPromotableItems().getSku() != null) {
            sku = new SimpleSkuWrapper(promotion.getPromotableItems().getSku());
            quantity = promotion.getPromotableItems().getQuantity();

        }
        description = promotion.getDescription();
        discount = promotion.getDiscount();
        bundle = promotion.getPromotableItems().isBundle();
    }

}
