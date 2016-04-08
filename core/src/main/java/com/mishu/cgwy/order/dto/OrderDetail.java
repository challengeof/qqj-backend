package com.mishu.cgwy.order.dto;

import lombok.Data;

@Data
public class OrderDetail {
    private String url;
    private String name;
    private String productNumber;
    private float price;
    private int number;
    private Long productId;

}
