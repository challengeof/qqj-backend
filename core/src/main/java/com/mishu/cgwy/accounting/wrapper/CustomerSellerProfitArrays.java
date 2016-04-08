package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

/**
 * Created by xiao1zhao2 on 15/12/4.
 */
@Data
public class CustomerSellerProfitArrays {

    private String[] sellerNames;
    private String[] warehouseNames;
    private Long[] restaurantIds;
    private String[] restaurantNames;
    private String[] receiverNames;
    private String[] telephones;
    private String[] categories;
    private ProfitWrapper[][] profits;

    private int page;
    private int pageSize;
    private Long total;

    public CustomerSellerProfitArrays() {
    }

    public CustomerSellerProfitArrays(String[] sellerNames, String[] warehouseNames, Long[] restaurantIds, String[] restaurantNames, String[] receiverNames, String[] telephones, String[] categories, ProfitWrapper[][] profits) {
        this.sellerNames = sellerNames;
        this.warehouseNames = warehouseNames;
        this.restaurantIds = restaurantIds;
        this.restaurantNames = restaurantNames;
        this.receiverNames = receiverNames;
        this.telephones = telephones;
        this.categories = categories;
        this.profits = profits;
    }

    public CustomerSellerProfitArrays(String[] sellerNames, String[] warehouseNames, Long[] restaurantIds, String[] restaurantNames, String[] receiverNames, String[] telephones, String[] categories, ProfitWrapper[][] profits, int page, int pageSize, Long total) {
        this.sellerNames = sellerNames;
        this.warehouseNames = warehouseNames;
        this.restaurantIds = restaurantIds;
        this.restaurantNames = restaurantNames;
        this.receiverNames = receiverNames;
        this.telephones = telephones;
        this.categories = categories;
        this.profits = profits;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}
