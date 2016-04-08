package com.mishu.cgwy.product.controller;

import lombok.Data;

/**
 * Created by bowen on 15/11/30.
 */
@Data
public class DynamicPriceCandidatesRequest {

    private Long organizationId;
    private String name;
    private Long warehouse;

    private int page = 0;
    private int pageSize = 15;
}
