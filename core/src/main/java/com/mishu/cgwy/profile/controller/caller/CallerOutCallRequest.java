package com.mishu.cgwy.profile.controller.caller;

import lombok.Data;

/**
 * Created by king-ck on 2015/10/13.
 */
@Data
public class CallerOutCallRequest {

    private String enterpriseId;
    private String hotline;
    private String crmId;
    private String cno;
    private String pwd;
    private String customerNumber;

}
