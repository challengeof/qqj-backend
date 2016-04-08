package com.mishu.cgwy.promotion.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by challenge on 15/12/28.
 */
@Data
public class PromotionListRequest {

    private Integer promotionType;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private int page = 0;

    private int pageSize = 100;
}
