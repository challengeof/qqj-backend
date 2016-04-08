package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.product.controller.SkuRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 11:11 AM
 */
@Data
public class ProductRequest {
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;

    private boolean discrete;

    private List<Long> mediaFileIds = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private String details;

    private String specification;//规格

    private Integer shelfLife;   //保质期

    private Long organizationId;

    private SkuRequest skuRequest;
}
