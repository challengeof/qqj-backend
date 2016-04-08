package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.stock.domain.AvgCostHistory;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 18/9/15.
 */
@Data
public class AvgCostHistoryWrapper {

    private Long id;

    private SkuWrapper sku;

    private SimpleCityWrapper city;

    private int quantity;

    private BigDecimal avgCost;

    private BigDecimal amount;

    private Date date;

    public AvgCostHistoryWrapper(AvgCostHistory avgCostHistory) {
        this.id = avgCostHistory.getId();
        this.sku = new SkuWrapper(avgCostHistory.getSku());
        this.city = new SimpleCityWrapper(avgCostHistory.getCity());
        this.quantity = avgCostHistory.getQuantity();
        this.avgCost = avgCostHistory.getAvgCost();
        this.amount = avgCostHistory.getAmount();
        this.date = avgCostHistory.getDate();
    }
}
