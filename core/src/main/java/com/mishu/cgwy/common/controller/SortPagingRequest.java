package com.mishu.cgwy.common.controller;

import lombok.Data;

/**
 * Created by king-ck on 2016/1/7.
 */
@Data
public class SortPagingRequest {
    protected int page = 0;
    protected int pageSize = 100;
    protected String sortField = "id";
    protected boolean asc = false;
}
