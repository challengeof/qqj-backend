package com.mishu.cgwy.order.constants;

import lombok.Data;

/**
 * Created by bowen on 15/9/14.
 */
@Data
public class CancelOrderReasonRequest {

    private Long orderId;

    private Integer reasonId;

    private String memo;
}
