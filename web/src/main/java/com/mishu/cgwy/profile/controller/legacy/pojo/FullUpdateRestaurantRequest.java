package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
public class FullUpdateRestaurantRequest {
    private Long id;
    private String name;
    private String telephone;
    private String realname;
    private Integer type;
    private String license;
    private Long regionId;
    private String address;

}
