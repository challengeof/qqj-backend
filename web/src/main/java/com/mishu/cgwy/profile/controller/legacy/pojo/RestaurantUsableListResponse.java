package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.dto.RestaurantUsableListResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by kaicheng on 3/18/15.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RestaurantUsableListResponse extends RestError {
    List<RestaurantUsableListResponseData> restaurantList;
}
