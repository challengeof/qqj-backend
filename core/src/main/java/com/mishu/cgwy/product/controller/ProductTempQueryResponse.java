package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.util.List;
/**
 * Created by bowen on 15-6-5.
 */
@Data
public class ProductTempQueryResponse {
    private long total;
    private int page;
    private int pageSize;

    private List<ChangeDetailResponse> changeDetailResponses;
}
