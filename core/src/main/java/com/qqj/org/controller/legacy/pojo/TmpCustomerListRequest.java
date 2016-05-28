package com.qqj.org.controller.legacy.pojo;

import com.qqj.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmpCustomerListRequest extends PageRequest {

    private Integer level;

    private String name;

    private String username;

    private Long team;

    private String telephone;

    private Short status;

    private String certificateNumber;

    private Short stage;
}
