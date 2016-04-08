package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/9/15.
 */
@Deprecated
@Data
public class RefundObjRequest {

    private Integer reasonId;

    private List<SkuRefundRequest> skuRefundRequests = new ArrayList<>();
}
