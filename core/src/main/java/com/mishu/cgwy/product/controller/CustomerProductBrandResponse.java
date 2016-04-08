package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CustomerProductBrandResponse {
    private long total;
    private List<BrandWrapper> brands = new ArrayList<>();
}
