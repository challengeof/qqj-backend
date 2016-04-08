package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListResponse extends RestError {
    private List<RestaurantListResponseData> restaurantList = new ArrayList<RestaurantListResponseData>();

    public List<RestaurantListResponseData> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<RestaurantListResponseData> restaurantList) {
        this.restaurantList = restaurantList;
    }


}
