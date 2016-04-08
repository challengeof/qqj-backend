package com.mishu.cgwy.profile.controller.caller;

import lombok.Data;

/**
 * Created by king-ck on 2015/10/14.
 */
@Data
public class CallerSendSmsRequest {
    private String   hotline;
    private String   enterpriseId;
    private String   userName;
    private String   seed;
    private String   pwd;
    private String   type="12";//12表示座席发送，8表示后台发送
    private String   mobile;
    private String   customerName;
    private String   msg;
    private String   cno;
}
