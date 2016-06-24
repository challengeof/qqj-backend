package com.qqj.org.wrapper;

import com.qqj.org.domain.Stock;
import com.qqj.org.domain.TmpStock;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangguodong on 16/6/24.
 */
@Getter
@Setter
public class StockWrapper {

    private Long productId;

    private String productName;

    private Integer quantity;

    public StockWrapper(Stock stock) {
        this.productId = stock.getProduct().getId();
        this.productName = stock.getProduct().getName();
        this.quantity = stock.getQuantity();
    }

    public StockWrapper(TmpStock stock) {
        this.productId = stock.getProduct().getId();
        this.productName = stock.getProduct().getName();
        this.quantity = stock.getQuantity();
    }
}
