package com.mishu.cgwy.profile.dto;

import lombok.Data;

/**
 * Created by challenge on 16/1/21.
 */
@Data
public class RestaurantTypeRequest {

    private String name;
    private Long parentRestaurantTypeId;
    private int status;
}
