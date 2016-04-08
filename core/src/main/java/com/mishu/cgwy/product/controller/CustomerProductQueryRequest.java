package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;


@Data
public class CustomerProductQueryRequest {

    private Long cityId;
    private Long brandId;
    private Long categoryId;
    private String name;

    private int page = 0;
    private int pageSize = 10;
    private String sortProperty = Constants.SORT_SELL_COUNT;
    private String sortDirection = Constants.ORDER_DESC;
}
