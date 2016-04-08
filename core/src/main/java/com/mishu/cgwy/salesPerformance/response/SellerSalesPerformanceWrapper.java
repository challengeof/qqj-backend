package com.mishu.cgwy.salesPerformance.response;

/**
 * Created by xiao1zhao2 on 15/12/14.
 */

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerSalesPerformanceWrapper {

    private Long sellerId;
    private String sellerName;
    private int currentNewCustomers;
    private int _1NewCustomers;
    private int _2NewCustomers;
    private int _3NewCustomers;
    private int currentOrders;
    private int _1Orders;
    private int _2Orders;
    private int _3Orders;
    private BigDecimal currentSalesAmount;
    private BigDecimal _1SalesAmount;
    private BigDecimal _2SalesAmount;
    private BigDecimal _3SalesAmount;
    private BigDecimal currentAvgCostAmount;
    private BigDecimal _1AvgCostAmount;
    private BigDecimal _2AvgCostAmount;
    private BigDecimal _3AvgCostAmount;
    private BigDecimal currentProfitAmount;
    private BigDecimal _1ProfitAmount;
    private BigDecimal _2ProfitAmount;
    private BigDecimal _3ProfitAmount;
    private BigDecimal currentProfitRate;
    private BigDecimal _1ProfitRate;
    private BigDecimal _2ProfitRate;
    private BigDecimal _3ProfitRate;

    public SellerSalesPerformanceWrapper() {
    }

    public SellerSalesPerformanceWrapper(Long sellerId, String sellerName, int currentNewCustomers, int _1NewCustomers, int _2NewCustomers, int _3NewCustomers, int currentOrders, int _1Orders, int _2Orders, int _3Orders, BigDecimal currentSalesAmount, BigDecimal _1SalesAmount, BigDecimal _2SalesAmount, BigDecimal _3SalesAmount, BigDecimal currentAvgCostAmount, BigDecimal _1AvgCostAmount, BigDecimal _2AvgCostAmount, BigDecimal _3AvgCostAmount) {

        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.currentNewCustomers = currentNewCustomers;
        this._1NewCustomers = _1NewCustomers;
        this._2NewCustomers = _2NewCustomers;
        this._3NewCustomers = _3NewCustomers;
        this.currentOrders = currentOrders;
        this._1Orders = _1Orders;
        this._2Orders = _2Orders;
        this._3Orders = _3Orders;
        this.currentSalesAmount = currentSalesAmount;
        this._1SalesAmount = _1SalesAmount;
        this._2SalesAmount = _2SalesAmount;
        this._3SalesAmount = _3SalesAmount;
        this.currentAvgCostAmount = currentAvgCostAmount;
        this._1AvgCostAmount = _1AvgCostAmount;
        this._2AvgCostAmount = _2AvgCostAmount;
        this._3AvgCostAmount = _3AvgCostAmount;
        this.currentProfitAmount = currentSalesAmount.subtract(currentAvgCostAmount);
        this._1ProfitAmount = _1SalesAmount.subtract(_1AvgCostAmount);
        this._2ProfitAmount = _2SalesAmount.subtract(_2AvgCostAmount);
        this._3ProfitAmount = _3SalesAmount.subtract(_3AvgCostAmount);
        this.currentProfitRate = this.currentSalesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this.currentProfitAmount.divide(this.currentSalesAmount, 4, BigDecimal.ROUND_HALF_UP);
        this._1ProfitRate = this._1SalesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this._1ProfitAmount.divide(this._1SalesAmount, 4, BigDecimal.ROUND_HALF_UP);
        this._2ProfitRate = this._2SalesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this._2ProfitAmount.divide(this._2SalesAmount, 4, BigDecimal.ROUND_HALF_UP);
        this._3ProfitRate = this._3SalesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this._3ProfitAmount.divide(this._3SalesAmount, 4, BigDecimal.ROUND_HALF_UP);
    }
}
