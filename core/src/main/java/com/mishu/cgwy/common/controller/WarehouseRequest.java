package com.mishu.cgwy.common.controller;

import lombok.Data;

/**
 * Created by wangwei on 15/7/17.
 */
@Data
public class WarehouseRequest {

    private Long id;
    private Long cityId;
    private String name;
    private Long depotId;

    private Boolean isDefault;
    private boolean active;

}
