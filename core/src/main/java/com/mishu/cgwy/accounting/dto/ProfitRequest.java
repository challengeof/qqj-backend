package com.mishu.cgwy.accounting.dto;

import com.mishu.cgwy.request.Request;
import lombok.Data;

import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/12/03.
 */
@Data
public class ProfitRequest extends Request {

    private Long cityId;
    private Long warehouseId;
    private int accountReceivableType = Integer.MAX_VALUE;
    private int restaurantStatus = Integer.MAX_VALUE;
    private Long categoryId;

    private Long orderId;
    private Long skuId;
    private String skuName;
    private Long restaurantId;
    private String restaurantName;
    private String customerName;
    private String sellerName;

    private Date startOrderDate;
    private Date endOrderDate;
    private Date startReceiveDate;
    private Date endReceiveDate;

    private int page = 0;
    private int pageSize = 100;
}
