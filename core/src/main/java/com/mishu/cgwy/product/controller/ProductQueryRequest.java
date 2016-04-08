package com.mishu.cgwy.product.controller;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class ProductQueryRequest {
    private Long id;
    private String name;
    private Long brandId;
    private Long categoryId;
    private Long cityId;
    private Long organizationId;
    private Integer status;

    private int page = 0;
    private int pageSize = 100;

}
