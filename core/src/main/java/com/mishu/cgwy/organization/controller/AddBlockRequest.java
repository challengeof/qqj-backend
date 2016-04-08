package com.mishu.cgwy.organization.controller;

import lombok.Data;

/**
 * Created by xingdong on 15/7/7.
 */
@Data
public class AddBlockRequest {
    private String blockName;

    private Long cityId;

    private Long warehouseId;

    private Boolean active;


    private String pointStr;
}
