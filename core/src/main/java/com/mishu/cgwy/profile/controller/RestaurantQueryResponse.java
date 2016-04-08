package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.profile.dto.RestaurantSummary;
import com.mishu.cgwy.profile.vo.RestaurantAuditReviewVo;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 11:23 AM
 */
@Data
public class RestaurantQueryResponse {

    private long total;

    private List<RestaurantWrapper> restaurants = new ArrayList<RestaurantWrapper>();

    private Map<Long, BigDecimal> consumption = new HashMap<>();

//    private Map<Long, RestaurantAuditReviewVo> auditReviewVo = new HashMap<>();

    private int page = 0;

    private int pageSize = 100;

//    private RestaurantSummary restaurantSummary;
}
