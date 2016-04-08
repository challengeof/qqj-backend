package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class SkuQueryResponse {
    private long total;
    private int page;
    private int pageSize;

    private List<SkuVo> skus;


}
