package com.mishu.cgwy.purchase.controller;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.util.Date;

@Data
public class CutOrderListRequest {

    private Long cityId;

    private Long organizationId;

    private Long depotId;

    private Short status;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
