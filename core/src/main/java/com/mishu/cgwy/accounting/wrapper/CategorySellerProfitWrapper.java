package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/1.
 */
@Data
public class CategorySellerProfitWrapper {

    private String sellerName;
    private String categoryName;
    private ProfitWrapper profit;

    public CategorySellerProfitWrapper() {
    }

    public CategorySellerProfitWrapper(String sellerName, String categoryName, ProfitWrapper profit) {
        this.sellerName = sellerName;
        this.categoryName = categoryName;
        this.profit = profit;
    }
}
