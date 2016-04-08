package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class GroupStockOutWrapper {

    private Long id;

    public GroupStockOutWrapper(StockOut stockOut) {
        this.id = stockOut.getId();
    }
}

