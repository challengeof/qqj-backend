package com.mishu.cgwy.profile.controller;

import lombok.Data;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 11:54 AM
 */
@Data
public class RestaurantUpdateRequest {
    private String name;
    private Long zoneId;
    private String address;
    private String streeNumer;

    private String contact;
    private String telephone;
    private String wgs84Point;
    private Integer status;

    private Long customerZoneId;
    private Long blockId;
    private Long type2;
    private Integer restaurantReason;
}
