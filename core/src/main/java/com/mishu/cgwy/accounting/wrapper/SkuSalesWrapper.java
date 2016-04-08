package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by xiao1zhao2 on 15/11/30.
 */
@Data
public class SkuSalesWrapper {

    private Long skuId;
    private String skuName;
    private String categoryName;
    private int capacityInBundle = 1;
    private String skuSingleUnit;
    private int quantity;
    private String skuBundleUnit;
    private BigDecimal bundleQuantity;
    private BigDecimal salesAmount;

    public SkuSalesWrapper() {
    }

    public SkuSalesWrapper(AccountReceivableItem item) {

        Sku sku = item.getSku();
        this.skuId = sku.getId();
        this.skuName = sku.getName();
        this.categoryName = sku.getProduct().getCategory().getParentCategory().getParentCategory().getName();
        this.capacityInBundle = sku.getCapacityInBundle();
        this.skuSingleUnit = item.getSku().getSingleUnit();
        if (item.getAccountReceivable().getType() == AccountReceivableType.SELL.getValue()) {
            this.quantity = item.getQuantity();
            this.salesAmount = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
        } else {
            this.quantity = item.getQuantity() * (-1);
            this.salesAmount = item.getPrice().multiply(new BigDecimal(item.getQuantity())).negate();
        }
        this.skuBundleUnit = sku.getBundleUnit();
        this.bundleQuantity = new BigDecimal(this.quantity).divide(new BigDecimal(this.capacityInBundle), 2, BigDecimal.ROUND_CEILING);

    }

    public SkuSalesWrapper(Long skuId, String skuName, String categoryName, int capacityInBundle, String skuSingleUnit, int quantity, String skuBundleUnit, BigDecimal salesAmount) {
        this.skuId = skuId;
        this.skuName = skuName;
        this.categoryName = categoryName;
        this.capacityInBundle = capacityInBundle;
        this.skuSingleUnit = skuSingleUnit;
        this.quantity = quantity;
        this.skuBundleUnit = skuBundleUnit;
        this.bundleQuantity = new BigDecimal(this.quantity).divide(new BigDecimal(this.capacityInBundle), 2, BigDecimal.ROUND_CEILING);
        this.salesAmount = salesAmount;
    }

    public void merge(SkuSalesWrapper sales) {
        this.quantity += sales.getQuantity();
        this.bundleQuantity = new BigDecimal(this.quantity).divide(new BigDecimal(this.capacityInBundle), 2, BigDecimal.ROUND_CEILING);
        this.salesAmount = this.salesAmount.add(sales.getSalesAmount());
    }

}
