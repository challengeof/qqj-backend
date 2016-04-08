package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class StockInRequest {

    private int stockInType = Integer.MAX_VALUE;
    private int stockInStatus = Integer.MAX_VALUE;
    private int purchaseOrderType = Integer.MAX_VALUE;
    private int sellReturnType = Integer.MAX_VALUE;
    private Integer saleReturn;

    private Long cityId;
    private Long depotId;
    private Long stockInId;
    private Long skuId;
    private String skuName;

    private Long sourceId;
    private Long purchaseOrderId;
    private Long vendorId;
    private String vendorName;
    private Long sellReturnId;
    private Long OrderId;

    private Long sourceDepotId;
    private Long targetDepotId;
    private Long transferId;

    private Date startCreateDate;
    private Date endCreateDate;
    private Date startReceiveDate;
    private Date endReceiveDate;

    private Boolean outPrint;

    private int page = 0;
    private int pageSize = 100;

}
