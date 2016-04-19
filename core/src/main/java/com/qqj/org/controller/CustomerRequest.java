package com.qqj.org.controller;

import com.qqj.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest extends PageRequest {

    private String name;

    private String founder;

    private String telephone;

}
