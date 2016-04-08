package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.stock.domain.SellCancel;
import com.mishu.cgwy.stock.domain.SellCancelType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/10/14.
 */
@Data
public class SellCancelRequest {

    private Long orderId;
    private int type = SellCancelType.CUSTOMER_CANCEL.getValue();
    private List<SellCancelItemRequest> sellCancelItemRequest = new ArrayList<>();
}
