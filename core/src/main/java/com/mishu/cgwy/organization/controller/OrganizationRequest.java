package com.mishu.cgwy.organization.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/7/6.
 */
@Data
public class OrganizationRequest {

    private Long id;

    private String name;

    private String telephone;

//    private Long serviceAdminId;

    private Long cityId;

    private boolean enable;

    private List<Long> blockIds = new ArrayList<Long>();

    private List<String> cityWarehouseBlockIds = new ArrayList<>();

}
