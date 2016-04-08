package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

/**
 * User: chengzheng
 * Date: 3/2/16
 * Time: 13:23 PM
 */
@Data
public class CreateRestaurantRequest {
    private String name;
    private Long regionId;
    private String address;
    private String license;
    private String realname;
    private String telephone;
    private Integer type;

}
