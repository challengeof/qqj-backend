package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bowen on 16/1/26.
 */
@Data
@EqualsAndHashCode
public class RestaurantTypeVo {

    private Long id;

    private String name;

    private String hierarchyName;

    private Long parentRestaurantTypeId;

    private RestaurantTypeStatus status = RestaurantTypeStatus.INACTIVE;

}
