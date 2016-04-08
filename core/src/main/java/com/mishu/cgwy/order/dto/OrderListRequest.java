package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderListRequest {
    private Long restaurantId;
    private int status;
    private String orderNumber;
    private List<ReturnDetail> returnDetail = new ArrayList<>();
    private Long orderId;

}
