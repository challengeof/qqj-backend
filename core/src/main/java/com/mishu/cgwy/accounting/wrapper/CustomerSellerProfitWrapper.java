package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/4.
 */
@Data
public class CustomerSellerProfitWrapper {

    private Long restaurantId;
    private String categoryName;
    private ProfitWrapper profit;

    public CustomerSellerProfitWrapper() {
    }

    public CustomerSellerProfitWrapper(Long restaurantId, String categoryName, ProfitWrapper profit) {
        this.restaurantId = restaurantId;
        this.categoryName = categoryName;
        this.profit = profit;
    }
}
