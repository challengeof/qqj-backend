package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/10/14.
 */
@Data
public class SellReturnRequest {

    private Long orderId;
    private List<SellReturnItemRequest> sellReturnItemRequests = new ArrayList<>();
}
