package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartAddRequest {
    private List<CartAddData> cartList = new ArrayList<CartAddData>();

}
