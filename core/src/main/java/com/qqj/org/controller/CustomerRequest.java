package com.qqj.org.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    private Long team;

    private String certificateNumber;

    private String name;

    private String username;

    private String telephone;

    private String address;

    private Long parent;

    private Short level;

    private boolean top;
}
