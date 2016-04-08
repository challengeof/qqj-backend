package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-7-6.
 */
@Data
public class RefundRequest {

    private Long orderId;
    private List<SkuRefundRequest> requests = new ArrayList<>();

}
