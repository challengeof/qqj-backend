package com.mishu.cgwy.accounting.vo;

import com.mishu.cgwy.accounting.domain.Payment;
import com.mishu.cgwy.accounting.enumeration.PaymentStatus;
import com.mishu.cgwy.accounting.wrapper.CollectionPaymentMethodWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentVo {

    private Long id;

    private String vendor;

    private PaymentStatus status;

    private String method;

    private BigDecimal amount;

    private Date payDate;

    private String creator;

    private String remark;
}
