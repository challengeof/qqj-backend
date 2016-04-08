package com.mishu.cgwy.push.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-7-29.
 */
@Data
public class PrecisePushRequest {

    private List<Long> restaurantIds = new ArrayList<>();
    private String title;
    private String message;
}
