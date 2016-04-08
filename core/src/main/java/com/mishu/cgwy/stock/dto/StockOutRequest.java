package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class StockOutRequest {

    private int stockOutType = Integer.MAX_VALUE;
    private int stockOutStatus = Integer.MAX_VALUE;
    private Boolean settle = null;
    private Long cityId;
    private Long depotId;
    private Long warehouseId;
    private Long blockId;
    private List<Long> blockIds =  new ArrayList<>();
    private Long stockOutId;
    private Long orderGroupId;
    private Boolean orderGroupIsNull;
    private Date expectedArrivedDate;
    private Long sourceId;  //  order,  transfer, returnNote
    private Long skuId;
    private String skuName;
    private Date startSendDate;
    private Date endSendDate;

    private Long orderId;
    private Integer orderStatus;
    private Long trackerId;
    private String customerName;
    private Date startOrderDate;
    private Date endOrderDate;
    private Date startReceiveDate;
    private Date endReceiveDate;
    private Date startSettleDate;
    private Date endSettleDate;

    private Long purchaseId;
    private Long vendorId;
    private String vendorName;

    private Long transferId;
    private Long sourceDepotId;
    private Long targetDepotId;
    private Date startTransferDate;
    private Date endTransferDate;
    private Date startAuditDate;
    private Date endAuditDate;

    private Boolean outPrint;
    private Boolean pickPrint;

    private int stockOutItemStatus = Integer.MAX_VALUE;

    private int page = 0;
    private int pageSize = 100;

}
