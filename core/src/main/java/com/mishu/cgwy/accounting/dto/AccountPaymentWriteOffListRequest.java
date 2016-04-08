package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPaymentWriteOffListRequest {

    private Long cityId;

    private Long purchaseVendorId;

    private Long vendorId;

    private Short status;

    private Long purchaseOrderId;

    private Long stockInId;

    private Long stockOutId;

    private Date stockInStartDate;

    private Date stockInEndDate;

    private Date writeOffStartDate;

    private Date writeOffEndDate;

    private Date writeOffCanceledStartDate;

    private Date writeOffCanceledEndDate;

    private BigDecimal minAccountPayableAmount;

    private BigDecimal maxAccountPayableAmount;

    private Short accountPayableType;

    private String writeOffer;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
