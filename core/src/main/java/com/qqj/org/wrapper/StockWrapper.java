package com.qqj.org.wrapper;

import com.qqj.org.domain.Stock;
import com.qqj.org.domain.StockItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 16/6/24.
 */
@Getter
@Setter
public class StockWrapper {

    private List<StockItemWrapper> stockItems = new ArrayList<>();

    public StockWrapper(Stock stock) {
        for (StockItem stockItem : stock.getStockItems()) {
            stockItems.add(new StockItemWrapper(stockItem));
        }
    }
}
