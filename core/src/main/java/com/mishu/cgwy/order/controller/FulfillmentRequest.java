package com.mishu.cgwy.order.controller;

import lombok.Data;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:17 PM
 */
@Data
public class FulfillmentRequest {
    private Boolean scheduleNow;
    private Boolean freeShipping;
}
