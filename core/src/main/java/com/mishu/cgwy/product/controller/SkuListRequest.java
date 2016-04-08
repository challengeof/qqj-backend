package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

@Data
public class SkuListRequest {
    private Long cityId;

    private Long organizationId;

    private Long vendorId;

    private Long categoryId;

    private String productName;

    private Long brandId;

    private Integer status;

    private int page = 0;

    private int pageSize = 100;
}
