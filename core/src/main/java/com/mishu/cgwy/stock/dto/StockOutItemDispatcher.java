package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

/**
 * Created by admin on 2015/11/9.
 */

@Data
public class StockOutItemDispatcher {

    private int index = 0;

    private Sku sku;

    private int quantity = 0;

    private String restaurantName;

    private String trackerName;

    private  Long vendorId;

    private String vendorName;

    private Long stockOutId;

    private Long orderId;

    private String expectedArrivedDate;

    private String unit;

    private String displayName;
}
