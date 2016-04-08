package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.accounting.dto.CollectionmentData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 15/9/22.
 */
@Data
public class StockOutData {

    private int stockOutType;
    private Long stockOutId;
    private BigDecimal receiveAmount;
    private boolean settle;
    private List<StockOutItemData> stockOutItems;
    private List<CollectionmentData> collectionments;
    private Set<Long> stockOutIds;
    private int type;//0 送货收款 1 收款

}
