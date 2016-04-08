package com.mishu.cgwy.push.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by bowen on 15/9/23.
 */
@Data
public class Notification {

    private List<Long> customerIds;

    //app推送消息时候的标题
    private String title;

    //推送的内容
    private String description;

    //app推送消息附带的链接.
    private String url;

    //app或微信的唯一id
    private String baiduChannelId;
}

