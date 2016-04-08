package com.mishu.cgwy.admin.dto;

import lombok.Data;

/**
 * Created by wangwei on 15/7/8.
 */
@Data
public class VendorQueryRequest {

    private Long cityId;
    private Long organizationId;
    private Long vendorId;

    private int page = 0;
    private int pageSize = Integer.MAX_VALUE;
}
