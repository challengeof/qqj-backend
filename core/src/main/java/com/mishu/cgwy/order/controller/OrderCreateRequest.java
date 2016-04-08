package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/12/1.
 */
@Data
public class OrderCreateRequest {

    private Long restaurantId;

    private String remark;

    private Long type;

    private List<OrderRequest> requests = new ArrayList<>();

}
