package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.util.Date;

@Data
public class VendorAccountDetailListRequest {

    private Long cityId;

    private Long vendorId;

    private Short purchaseOrderType;

    private Short operationType;

    private String productName;

    private Long purchaseOrderId;

    private Date purchaseOrderDateStart;

    private Date purchaseOrderDateEnd;

    private Date operationDateStart;

    private Date operationDateEnd;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
