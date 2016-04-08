package com.mishu.cgwy.profile.controller.caller;

import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 3:52 PM
 */
@Data
public class CallerListRequest {

    private Long callerId;
    private String name;
    private Integer gender;
    private String phone;

    private String company;  //公司名
    private String receiver; //联系人

    private Date createDate;
    private Date modifyDate;

    private int page = 0;
    private int pageSize = 100;

    private String sortField = "id";
    private boolean asc = false;



}
