package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.common.wrapper.WarehouseWrapper;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 3/1/15
 * Time: 10:24 PM
 */
@Data
public class SimpleRestaurantWrapper {
    private Long id;
    private String name;
    private AddressWrapper address;
    private String license;
    private int status;

    private String receiver; //联系人
    private String telephone; //联系人电话

    private RestaurantTypeWrapper type;//餐馆类型

    public SimpleRestaurantWrapper() {

    }

    public SimpleRestaurantWrapper(Restaurant restaurant) {
        id = restaurant.getId();
        name = restaurant.getName();
        if (restaurant.getAddress() != null) {
            address = new AddressWrapper(restaurant.getAddress());
        }

        license = restaurant.getLicense();
        status = restaurant.getStatus();
        receiver = restaurant.getReceiver();
        telephone = restaurant.getTelephone();
        type = restaurant.getType() != null ? new RestaurantTypeWrapper(restaurant.getType()) : null;
    }

}
