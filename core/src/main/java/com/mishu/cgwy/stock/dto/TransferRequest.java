package com.mishu.cgwy.stock.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TransferRequest {

    private Long cityId;

    private Long id;

    private Short status;

    private Long sourceDepotId;

    private Long targetDepotId;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
