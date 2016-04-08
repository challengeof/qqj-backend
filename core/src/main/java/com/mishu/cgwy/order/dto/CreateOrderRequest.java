package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long restaurantId;
    private int fromCart;
    private int payType;
    private List<CartAddData> productList = new ArrayList<CartAddData>();

}
