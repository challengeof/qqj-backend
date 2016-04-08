package com.mishu.cgwy.order.dto;

import lombok.Data;

@Data
public class OrderReturnDetail {
    private String url;
    private String productNumber;
    private float price;
    private int number;
    private int type;

}
