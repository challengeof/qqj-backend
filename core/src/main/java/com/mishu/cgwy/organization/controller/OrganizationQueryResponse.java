package com.mishu.cgwy.organization.controller;

import com.mishu.cgwy.order.dto.OrderStatistics;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;

import java.util.List;

/**
 * Created by wangwei on 15/7/2.
 */
@Data
public class OrganizationQueryResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<OrganizationVo> organizations;
}
