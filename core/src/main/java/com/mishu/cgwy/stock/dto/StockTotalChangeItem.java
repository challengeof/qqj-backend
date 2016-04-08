package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by admin on 2015/9/18.
 */
@Data
public class StockTotalChangeItem {

    private City city;
    private Sku sku;
    private int quantity;
    private BigDecimal price;
}
