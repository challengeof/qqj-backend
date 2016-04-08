package com.mishu.cgwy.organization.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by wangwei on 15/7/2.
 */

@Data
public class OrganizationQueryRequest {

    private Date createDate;
    private Boolean enable;
    private String name;
    private Long cityId;

    private int page = 0;
    private int pageSize = 100;
}
