package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/9/18.
 */
@Data
public class StockTotalChange {
    
    private List<StockTotalChangeItem> stockTotalChangeItems = new ArrayList<>();

    public void add(City city, Sku sku, BigDecimal price, int quantity) {
        boolean found = false;
        for (StockTotalChangeItem item : stockTotalChangeItems) {
            if (city.getId().equals(item.getCity().getId()) && sku.getId().equals(item.getSku().getId())
                    && ((price == null && item.getPrice() == null) || (price != null && item.getPrice() != null && price.compareTo(item.getPrice()) == 0))) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (found == false) {
            StockTotalChangeItem item = new StockTotalChangeItem();
            item.setCity(city);
            item.setPrice(price);
            item.setQuantity(quantity);
            item.setSku(sku);
            stockTotalChangeItems.add(item);
        }
    }
}
