package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.mishu.cgwy.profile.dto.RestaurantRefer;

/**
 * Created by kaicheng on 3/18/15.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RestaurantGetResponse extends RestError {
    RestaurantRefer restaurant;
}
