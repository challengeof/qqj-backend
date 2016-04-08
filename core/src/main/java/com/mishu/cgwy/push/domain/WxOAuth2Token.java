package com.mishu.cgwy.push.domain;

import lombok.Data;

/**
 * Created by bowen on 15/10/16.
 */
@Data
public class WxOAuth2Token {

    private String accessToken;

    private String openId;

    private Text content;

}
