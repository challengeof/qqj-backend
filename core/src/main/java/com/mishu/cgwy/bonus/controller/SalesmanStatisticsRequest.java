package com.mishu.cgwy.bonus.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 15/9/17.
 */
@Data
public class SalesmanStatisticsRequest {

    private Date start;

    private Date end;

    private Long adminUserId;
}
