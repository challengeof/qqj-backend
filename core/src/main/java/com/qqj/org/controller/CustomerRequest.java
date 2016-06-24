package com.qqj.org.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerRequest {

    private Long team;

    private String certificateNumber;

    private String name;

    private String telephone;

    private String address;

    private Short level;

    private List<StockInfo> stocks;
}
