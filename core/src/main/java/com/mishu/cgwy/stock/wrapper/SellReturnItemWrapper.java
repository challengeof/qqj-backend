package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.stock.domain.SellReturnItem;
import com.mishu.cgwy.stock.domain.SellReturnReason;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
public class SellReturnItemWrapper {

    private Long id;

    private int quantity;

    private SimpleSkuWrapper sku;

    private BigDecimal avgCost;

    private BigDecimal price;

    private String memo;

    private SellReturnReasonWrapper sellReturnReason;

    public SellReturnItemWrapper(){}

    public SellReturnItemWrapper(SellReturnItem sellReturnItem) {
        this.id = sellReturnItem.getId();
        this.quantity = sellReturnItem.getQuantity();
        this.avgCost = sellReturnItem.getAvgCost();
        this.price = sellReturnItem.getPrice();
        this.sellReturnReason = sellReturnItem.getSellReturnReason() != null ? new SellReturnReasonWrapper(sellReturnItem.getSellReturnReason()) : null;
        this.sku = new SimpleSkuWrapper(sellReturnItem.getSku());

        this.memo=sellReturnItem.getMemo();
    }
}
