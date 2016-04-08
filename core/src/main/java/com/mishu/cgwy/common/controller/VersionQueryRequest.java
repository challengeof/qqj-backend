package com.mishu.cgwy.common.controller;

import lombok.Data;

@Data
public class VersionQueryRequest {

    private Integer versionCode;
    private String versionName;
    private String comment;

    private int page = 0;
    private int pageSize = Integer.MAX_VALUE;
}
