package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.wrapper.ProductWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingdong on 15/7/11.
 */
@Data
public class ProductBatchResponse {
    private List<ProductWrapper> productWrappers = new ArrayList<>();

    private int errorNum;
}
