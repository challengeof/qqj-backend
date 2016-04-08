package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

/**
 * Created by kaicheng on 3/17/15.
 */
@Data
public class UpdateRestaurantRequest {
    private Long id;
    private String name;
    private String realname;
    private String telephone;

    private Double lat; //纬度
    private Double lng; //经度
    private String restaurantAddress; //地址
    private String restaurantStreetNumber; //街道
}
