package com.mishu.cgwy.profile.dto;

import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import lombok.Data;

/**
 * Created by kaicheng on 3/18/15.
 */
@Data
public class RestaurantRefer {
    private Long id;
    private String name;
    private String restaurantNumber;
    private Long regionId;
    private String address;
    private String license ="";
    private String realname;
    private String telephone;
    private Integer status;
    private Long type;
    private String typeMessage = "";
    private String recommendUserNumber = "";
    private String recommendName = "";

    public RestaurantRefer() {

    }

    public RestaurantRefer(RestaurantWrapper restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.restaurantNumber = "Number" + restaurant.getId();
        this.address = restaurant.getAddress().getAddress();
        this.license = restaurant.getLicense();
        this.realname = restaurant.getReceiver();
        this.telephone = restaurant.getTelephone();
        this.type = restaurant.getType() != null ? restaurant.getType().getId() : null;
        this.status = restaurant.getStatus().getValue();
    }


}
