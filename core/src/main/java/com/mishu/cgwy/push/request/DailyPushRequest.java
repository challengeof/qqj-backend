package com.mishu.cgwy.push.request;

import lombok.Data;

/**
 * Created by bowen on 15-7-29.
 */
@Data
public class DailyPushRequest {

    private Long id;
    private String tag;
    private String message;
}
