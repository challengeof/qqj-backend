package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.wrapper.ProductWrapper;
import com.mishu.cgwy.product.wrapper.SimpleProductWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class ProductQueryResponse {
    private long total;
    private int page;
    private int pageSize;

    private List<ProductWrapper> products;
}
