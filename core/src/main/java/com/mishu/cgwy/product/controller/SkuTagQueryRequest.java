package com.mishu.cgwy.product.controller;

import lombok.Data;

/**
 * Created by wangwei on 15/12/2.
 */
@Data
public class SkuTagQueryRequest extends SkuQueryRequest {

    private Long tagCityId;
}
