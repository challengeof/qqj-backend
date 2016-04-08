package com.mishu.cgwy.accounting.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/10/13.
 */
@Data
public class AccountReceivableRequest {
    private Long cityId;
    private Long depotId;
    private Long warehouseId;
    private Long trackerId;
    private int accountReceivableStatus = Integer.MAX_VALUE;
    private int accountReceivableType = Integer.MAX_VALUE;
    private int accountReceivableWriteoffStatus = Integer.MAX_VALUE;

    private Long sourceId;
    private Long orderId;
    private Long restaurantId;
    private Long skuId;
    private String skuName;
    private String customerName;
    private String sellerName;

    private Date startOrderDate;
    private Date endOrderDate;
    private Date startSendDate;
    private Date endSendDate;
    private Date startReceiveDate;
    private Date endReceiveDate;
    private Date startWriteoffDate;
    private Date endWriteoffDate;

    private List<Long> accountReceivableIds = new ArrayList<>();

    private Long accountReceivableWriteoffId;
    private Date cancelDate;
    private String type;

    private int page = 0;
    private int pageSize = 100;
}
