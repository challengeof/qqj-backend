package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/11/30.
 */
@Data
public class SkuProfitWrapper {

    private String warehouseName;
    private Long orderId;
    private Date orderDate;
    private Long skuId;
    private String skuName;
    private String categoryName;
    private Long restaurantId;
    private String restaurantName;
    private String sellerName;
    private String accountReceivableType;
    private String skuSingleUnit;
    private int quantity;
    private BigDecimal avgCost;
    private BigDecimal price;
    private BigDecimal avgCostAmount;
    private BigDecimal salesAmount;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private Date createDate;

    public SkuProfitWrapper() {
    }

    public SkuProfitWrapper(AccountReceivableItem item) {

        AccountReceivable receivable = item.getAccountReceivable();
        this.warehouseName = receivable.getRestaurant().getCustomer().getBlock().getWarehouse().getName();
        if (receivable.getType() == AccountReceivableType.SELL.getValue()) {
            this.orderId = receivable.getStockOut().getOrder().getId();
            this.orderDate = receivable.getStockOut().getOrder().getSubmitDate();
        } else if (receivable.getType() == AccountReceivableType.RETURN.getValue()) {
            this.orderId = receivable.getStockIn().getSellReturn().getOrder().getId();
            this.orderDate = receivable.getStockIn().getSellReturn().getOrder().getSubmitDate();
        }
        this.skuId = item.getSku().getId();
        this.skuName = item.getSku().getName();
        this.categoryName = item.getSku().getProduct().getCategory().getParentCategory().getParentCategory().getName();
        this.restaurantId = receivable.getRestaurant().getId();
        this.restaurantName = receivable.getRestaurant().getName();
        this.sellerName = receivable.getRestaurant().getCustomer().getAdminUser().getRealname();
        this.accountReceivableType = AccountReceivableType.fromInt(receivable.getType()).getName();
        this.skuSingleUnit = item.getSku().getSingleUnit();
        this.avgCost = item.getAvgCost();
        this.price = item.getPrice();
        this.avgCostAmount = item.getAvgCost().multiply(new BigDecimal(item.getQuantity()));
        if (receivable.getType() == AccountReceivableType.SELL.getValue()) {
            this.quantity = item.getQuantity();
            this.salesAmount = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            this.profitAmount = (item.getPrice().subtract(item.getAvgCost())).multiply(new BigDecimal(item.getQuantity()));
        } else if (receivable.getType() == AccountReceivableType.RETURN.getValue()) {
            this.quantity = item.getQuantity() * (-1);
            this.salesAmount = item.getPrice().multiply(new BigDecimal(item.getQuantity())).negate();
            this.profitAmount = (item.getPrice().subtract(item.getAvgCost())).multiply(new BigDecimal(item.getQuantity())).negate();
        }
        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this.profitAmount.divide(this.salesAmount, 4, BigDecimal.ROUND_HALF_UP);
        this.createDate = receivable.getCreateDate();
    }
}
