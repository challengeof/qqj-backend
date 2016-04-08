package com.mishu.cgwy.product.controller;

import lombok.Data;

/**
 * Created by wangguodong on 15/10/30.
 */
@Data
public class SkuCandidatesRequest {

    private Long organizationId;
    private String name;

    private int page = 0;
    private int pageSize = 15;
}
