package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPayableListRequest {

    private Long cityId;

    private Long purchaseVendorId;

    private Long vendorId;

    private Short status;

    private Long purchaseOrderId;

    private Long stockInId;

    private Long stockOutId;

    private Date startDate;

    private Date endDate;

    private Date writeOffStartDate;

    private Date writeOffEndDate;

    private BigDecimal minAccountPayableAmount;

    private BigDecimal maxAccountPayableAmount;

    //是否查询已完成销账的记录
    private Boolean includeWriteOff;

    private Short accountPayableType;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
