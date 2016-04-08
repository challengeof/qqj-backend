package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import lombok.Data;

@Data
public class RestaurantListResponseData {
    private Long id;
    private String telephone;
    private int status;
    private int regionId;
    private String name;
    private String address;
    private String license;
    private String realname;
    private Long type;
    private String typeMessage;
    private String restaurantNumber;
    private String recommendName = "";
    private String recommendNumber = "";

    private ZoneWrapper zone;
    

}