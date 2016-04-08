package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.order.constants.OrderHistoryConstants;
import lombok.Data;

/**
 * Created by kaicheng on 3/26/15.
 */
@Data
public class OrderHistoryRequest {
    private Long restaurantId;
    private String sort = OrderHistoryConstants.SORT_CREATE_TIME;
    private String order = OrderHistoryConstants.ORDER_DESC;
    private Integer page = OrderHistoryConstants.PAGE;
    private Integer rows = OrderHistoryConstants.ROWS;
    private Integer all;
}
