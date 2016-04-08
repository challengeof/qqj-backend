package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:11 PM
 */
@Data
public class OrderGroupRequest {
    private List<Long> orderIds =  new ArrayList<>();

    private String name;

    private Long trackerId;

    private Long agentId;

    private Long depotId;

    private Long organizationId;

    private Long cityId;

}
