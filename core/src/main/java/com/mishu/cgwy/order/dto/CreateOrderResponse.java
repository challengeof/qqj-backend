package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateOrderResponse extends RestError {
    private String orderNumber;
    private List<String> notEnough = new ArrayList<String>();
}
