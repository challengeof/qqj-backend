package com.qqj.org.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditCustomerRequest {

    //1-上级代理，2-创始人审批，3-总部审批
    private Short type;

    //0-不通过，1-通过。
    private Short result;

    //被审批人的id
    private Long tmpCustomerId;
}
