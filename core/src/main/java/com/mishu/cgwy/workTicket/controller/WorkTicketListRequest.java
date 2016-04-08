package com.mishu.cgwy.workTicket.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 16/2/29.
 */
@Data
public class WorkTicketListRequest {

    private Integer process;

    private Integer problemSources;

    private Date startDate;

    private Date endDate;

    private Long restaurantId;

    private String restaurantName;

    private Long orderId;

    private String consultants;

    private String consultantsTelephone;

    private Integer status;

    private int page = 0;

    private int pageSize = 100;

    private Long followUpId;
}
