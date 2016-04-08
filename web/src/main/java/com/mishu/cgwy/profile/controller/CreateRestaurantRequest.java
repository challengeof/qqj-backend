package com.mishu.cgwy.profile.controller;

import lombok.Data;

@Data
public class CreateRestaurantRequest {
    private String receiver;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantLicense;
    private Integer restaurantType;
}
