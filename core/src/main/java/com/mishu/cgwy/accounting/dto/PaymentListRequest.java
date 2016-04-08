package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentListRequest {

    private Long id;

    private Long cityId;

    private Long vendorId;

    private Short methodCode;

    private Date startDate;

    private Date endDate;

    private Short status;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private String creator;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
