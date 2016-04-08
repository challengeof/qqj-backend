package com.mishu.cgwy.organization.controller;

import lombok.Data;

/**
 * Created by xingdong on 15/7/6.
 */
@Data
public class BlockQueryRequest {
    private Long blockId;
    private String blockName;
    private Long warehouseId;
    private Integer status;
    private Long cityId;

    private int page = 0;
    private int pageSize = 100;
}
