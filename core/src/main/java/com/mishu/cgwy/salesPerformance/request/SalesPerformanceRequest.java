package com.mishu.cgwy.salesPerformance.request;

import lombok.Data;

import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
@Data
public class SalesPerformanceRequest {
    private Long cityId;
    private Date startDate;
    private Date endDate;
    private int page = 0;
    private int pageSize = 20;
}
