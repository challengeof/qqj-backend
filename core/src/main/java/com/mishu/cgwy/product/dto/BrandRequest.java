package com.mishu.cgwy.product.dto;

import lombok.Data;

@Data
public class BrandRequest {

    private Long brandId;
    private String brandName;
    private Integer status = Integer.MAX_VALUE;

    private int page = 0;
    private int pageSize = Integer.MAX_VALUE;
}
