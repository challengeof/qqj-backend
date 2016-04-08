package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 15/8/27.
 */
@Data
public class RestaurantAlarmResponse {

    private List<RestaurantWrapper> restaurants = new ArrayList<RestaurantWrapper>();

    private Map<Long, Long> alarmCount = new HashMap<>();
}
