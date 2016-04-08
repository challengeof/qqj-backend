package com.mishu.cgwy.accounting.wrapper;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by xiao1zhao2 on 15/12/09.
 */
@Data
public class ProfitWrapper {

    private BigDecimal salesAmount = BigDecimal.ZERO;
    private BigDecimal avgCostAmount = BigDecimal.ZERO;
    private BigDecimal profitAmount = BigDecimal.ZERO;
    private BigDecimal profitRate = BigDecimal.ZERO;

    public ProfitWrapper() {
    }

    public ProfitWrapper(BigDecimal salesAmount, BigDecimal avgCostAmount, BigDecimal profitAmount) {
        this.salesAmount = salesAmount;
        this.avgCostAmount = avgCostAmount;
        this.profitAmount = profitAmount;
        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this.profitAmount.divide(this.salesAmount, 4, BigDecimal.ROUND_CEILING);
    }

    public void merge(ProfitWrapper grossProfit) {
        this.salesAmount = this.salesAmount.add(grossProfit.getSalesAmount());
        this.avgCostAmount = this.avgCostAmount.add(grossProfit.getAvgCostAmount());
        this.profitAmount = this.profitAmount.add(grossProfit.getProfitAmount());
        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this.profitAmount.divide(this.salesAmount, 4, BigDecimal.ROUND_CEILING);
    }
}
