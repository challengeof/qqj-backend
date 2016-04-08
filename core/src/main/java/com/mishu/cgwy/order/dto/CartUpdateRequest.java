package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartUpdateRequest {
    List<CartUpdateData> cartList = new ArrayList<CartUpdateData>();

}
