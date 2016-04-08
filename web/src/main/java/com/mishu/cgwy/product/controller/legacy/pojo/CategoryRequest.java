package com.mishu.cgwy.product.controller.legacy.pojo;

import lombok.Data;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
public class CategoryRequest {
    private Long categoryId;
    private Integer all;
    private Integer page;
    private Integer rows;
}
