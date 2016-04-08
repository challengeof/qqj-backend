package com.mishu.cgwy.category.controller;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private Long parentCategoryId;
    private int status;
    private Long mediaFileId;
    private Boolean showSecond;

}
