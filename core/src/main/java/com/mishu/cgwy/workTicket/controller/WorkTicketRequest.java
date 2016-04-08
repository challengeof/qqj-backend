package com.mishu.cgwy.workTicket.controller;

import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 16/2/29.
 */
@Data
public class WorkTicketRequest {

    private Integer process;

    private Integer problemSources;

    private Long restaurantId;

    private String restaurantTelephone;

    private Long orderId;

    private String consultants;

    private String consultantsTelephone;

    private Integer status;

    private String content;

    private Long followUpId;

    private String username;
}
