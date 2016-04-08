package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2016/4/6.
 */
@Data
public class SkuSpecifyQueryRequest {
    private List<Long> skuIds;
}
