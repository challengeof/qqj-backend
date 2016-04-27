package com.qqj.barcode.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 16/4/26.
 */
@Data
public class QueryBarcodeRequest {

    private Long barcodeId;

    private Long barcodeItemId;

    private String bagCode;

    private String boxCode;

    private String expressNo;

    private Date startDate;

    private Date endDate;

    private int page = 0;

    private int pageSize = 50;
}
