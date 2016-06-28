package com.qqj.org.wrapper;

import com.qqj.org.domain.StockItem;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangguodong on 16/6/24.
 */
@Getter
@Setter
public class StockItemWrapper {

    private Long productId;

    private String productName;

    private Integer quantity;

    public StockItemWrapper(StockItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
    }
}
