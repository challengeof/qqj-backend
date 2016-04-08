package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.RestaurantType;
import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import lombok.Data;

@Data
public class RestaurantTypeWrapper {

    private Long id;

    private String name;

    private String hierarchyName;

    private Long parentRestaurantTypeId;

    private RestaurantTypeStatus status = RestaurantTypeStatus.INACTIVE;

    public RestaurantTypeWrapper() {

    }

    public RestaurantTypeWrapper(RestaurantType restaurantType) {
        this.id = restaurantType.getId();
        this.name = restaurantType.getName();
        this.hierarchyName = name;
        RestaurantType current = restaurantType;
        while (current.getParentRestaurantType() != null) {
            hierarchyName = current.getParentRestaurantType().getName() + "-" + hierarchyName;
            current = current.getParentRestaurantType();
        }
        this.status = RestaurantTypeStatus.fromInt(restaurantType.getStatus());
        this.parentRestaurantTypeId = restaurantType.getParentRestaurantType() == null ? null : restaurantType.getParentRestaurantType().getId();

    }
}
