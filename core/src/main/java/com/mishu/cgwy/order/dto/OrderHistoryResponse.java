package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 3/27/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderHistoryResponse extends RestError {
    private List<OrderHistoryItem> historyList = new ArrayList<OrderHistoryItem>();
    private Integer total;
}
