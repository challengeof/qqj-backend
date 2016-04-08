package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.request.Request;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SkuPriceHistoryListRequest extends Request {

    private Long skuId;

    private Integer type;

    private Boolean single;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private int page = 0;

    private int pageSize = 100;
}
