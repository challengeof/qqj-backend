package com.qqj.product.controller;

import com.qqj.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListRequest extends PageRequest {
    private String name;
}
