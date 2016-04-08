package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


//web
@Data
public class CustomerProductQueryResponse {
    private long total;
    private List<SkuWrapper> skus = new ArrayList<>();
}
