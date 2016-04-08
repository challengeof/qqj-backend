package com.mishu.cgwy.push.domain;

import lombok.Data;

/**
 * Created by bowen on 15/10/16.
 */
@Data
public class WxMessage {

    private String touser;

    private String msgtype;

    private Text text;
}
