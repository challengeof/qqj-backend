package com.mishu.cgwy.organization.controller;

import lombok.Data;

/**
 * Created by xingdong on 15/7/28.
 */
@Data
public class UpdateBlockQueryRequest {
    private String blockName;

    private Long cityId;

    private Long warehouseId;

    private Boolean active;

    private String pointStr;
}
