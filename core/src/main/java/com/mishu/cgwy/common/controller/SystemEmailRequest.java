package com.mishu.cgwy.common.controller;

import lombok.Data;

@Data
public class SystemEmailRequest {
    private Long cityId;
    private Integer type;
    private int page = 0;
    private int pageSize = 100;
}
