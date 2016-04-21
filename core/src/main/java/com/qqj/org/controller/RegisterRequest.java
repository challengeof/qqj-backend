package com.qqj.org.controller;

import lombok.Data;

/**
 * User: xudong
 * Date: 6/5/15
 * Time: 4:01 PM
 */
@Data
public class RegisterRequest {
    private String telephone;
    private String password;
    private String recommendNumber;
    private Long zoneId; //商圈(废弃)
    private Long cityId; //城市I

    private Double lat; //纬度
    private Double lng; //经度
    private boolean containsRestaurant = true;

    private String receiver;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantStreetNumber;
    private String restaurantLicense;
    private Integer restaurantType;
    private String sharerId;
//    private Integer shareType;
}
