package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.StockAdjustStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 15-9-15.
 */
@Data
public class StockAdjustWrapper {

    private Long id;
    private Long depotId;
    private String depotName;
    private Long skuId;
    private String skuName;
    private String skuSingleUnit;
    private String skuBundleUnit;
    private int skuCapacityInBundle;
    private BigDecimal taxRate;
    private int quantity = 0;
    private int adjustQuantity = 0;
    private Date expirationDate;
    private Date productionDate;
    private String shelfName;
    private StockAdjustStatus stockAdjustStatus;
    private Date auditDate;
    private BigDecimal avgCost;
    private String comment;
    private Date createDate;
    private Long creatorId;
    private String creatorName;
    private Long auditorId;
    private String auditorName;
}
