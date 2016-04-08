package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.stock.domain.StockOutItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutOrderItemWrapper {
    private Long id;

    private SimpleSkuWrapper sku;

    private BigDecimal price;

    private int quantity;

    private BigDecimal totalPrice;

    public StockOutOrderItemWrapper() {

    }

    public StockOutOrderItemWrapper(StockOutItem stockOutItem) {
        id = stockOutItem.getId();
        sku = new SimpleSkuWrapper(stockOutItem.getSku());
        price = stockOutItem.getPrice();
        quantity = stockOutItem.getRealQuantity();
        totalPrice = stockOutItem.getPrice().multiply(new BigDecimal(stockOutItem.getRealQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
