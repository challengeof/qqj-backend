package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 11:34 PM
 */
@Data
public class DynamicPriceQueryResponse {
    private long total;

    private int page;
    private int pageSize;

    private List<DynamicSkuPriceWrapper> dynamicPrices;
}
