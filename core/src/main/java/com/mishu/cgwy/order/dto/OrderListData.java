package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderListData {
    private String orderNumber;
    private float price;
    private String address;
    private String name;// restaurant name
    private String restaurantName;
    private String realname;// accept person name
    private String telephone;
    private int status;
    private List<OrderDetail> orderDetail = new ArrayList<OrderDetail>();
    private String restaurantNumber;
    private String createTime;
    private String adminTelephone;
    private String adminName;
    private String carNo;
    private String driverTelephone;
    private Long orderId;
    private Long userId;
    private BigDecimal shipping;
    private String payType;


}
