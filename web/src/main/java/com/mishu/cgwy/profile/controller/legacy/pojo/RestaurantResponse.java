package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;

/**
 * Created by kaicheng on 3/16/15.
 */


public class RestaurantResponse extends RestError {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
