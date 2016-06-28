package com.qqj.org.wrapper;

import com.qqj.org.domain.TmpStock;
import com.qqj.org.domain.TmpStockItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 16/6/24.
 */
@Getter
@Setter
public class TmpStockWrapper {

    private List<TmpStockItemWrapper> stockItems = new ArrayList<>();

    public TmpStockWrapper(TmpStock tmpStock) {
        for (TmpStockItem tmpStockItem : tmpStock.getTmpStockItems()) {
            stockItems.add(new TmpStockItemWrapper(tmpStockItem));
        }
    }
}
