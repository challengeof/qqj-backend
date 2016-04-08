package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.controller.OrderGroupsSkuTotal;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/8/25.
 */
@Data
public class StockOutGroupsSku {

    private SimpleStockOutGroupWrapper stockOutGroupWrapper;

    private List<OrderGroupsSkuTotal> orderGroupsSkuTotals = new ArrayList<>();

}
