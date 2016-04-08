package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/1.
 */
@Data
public class WarehouseCategoryProfitWrapper {

    private String warehouseName;
    private String categoryName;
    private ProfitWrapper profit;

    public WarehouseCategoryProfitWrapper() {
    }

    public WarehouseCategoryProfitWrapper(String warehouseName, String categoryName, ProfitWrapper profit) {
        this.warehouseName = warehouseName;
        this.categoryName = categoryName;
        this.profit = profit;
    }
}
