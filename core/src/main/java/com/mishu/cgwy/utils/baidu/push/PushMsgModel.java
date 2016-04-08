package com.mishu.cgwy.utils.baidu.push;

import lombok.Data;

/**
 * Created by linsen on 15/12/31.
 */
@Data
public class PushMsgModel {

    public static final int DEVICE_TYPE_ANDROID = 3;
    public static final int DEVICE_TYPE_IOS = 4;

    private String title;

    private String description;

    private String[] channelIds;

    private int deviceType;

    private int expires; //保留字段

}
