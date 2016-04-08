package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/1.
 */
@Data
public class CategorySellerProfitArrays {

    private String[] sellers;
    private String[] categories;
    private ProfitWrapper[][] profits;

    public CategorySellerProfitArrays() {
    }

    public CategorySellerProfitArrays(String[] sellers, String[] categories, ProfitWrapper[][] profits) {
        this.sellers = sellers;
        this.categories = categories;
        this.profits = profits;
    }
}
