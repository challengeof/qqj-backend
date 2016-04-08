package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/1.
 */
@Data
public class WarehouseCategoryProfitArrays {

    private String[] warehouses;
    private String[] categories;
    private ProfitWrapper[][] profits;

    public WarehouseCategoryProfitArrays() {
    }

    public WarehouseCategoryProfitArrays(String[] warehouses, String[] categories, ProfitWrapper[][] profits) {
        this.warehouses = warehouses;
        this.categories = categories;
        this.profits = profits;
    }
}
