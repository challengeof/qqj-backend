package com.qqj.purchase.wrapper;

import com.qqj.purchase.domain.PurchaseItem;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangguodong on 16/4/12.
 */
@Setter
@Getter
public class PurchaseItemWrapper {

    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    public PurchaseItemWrapper(PurchaseItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
    }
}
