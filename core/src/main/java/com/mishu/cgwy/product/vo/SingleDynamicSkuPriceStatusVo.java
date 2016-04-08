package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.inventory.domain.SingleDynamicSkuPriceStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/16.
 */
@Data
public class SingleDynamicSkuPriceStatusVo {

    private BigDecimal singleSalePrice;

    private boolean singleAvailable;

    private boolean singleInSale;
}
