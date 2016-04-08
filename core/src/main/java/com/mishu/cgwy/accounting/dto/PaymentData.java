package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentData {

    private Date payDate;

    private Long cityId;

    private Long vendorId;

    private Long methodId;

    private String remark;

    private BigDecimal amount;
}
