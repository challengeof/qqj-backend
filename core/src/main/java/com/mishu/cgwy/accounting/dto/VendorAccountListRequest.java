package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VendorAccountListRequest {

    private Long cityId;

    private Long vendorId;

    private Date statisticalDate;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
