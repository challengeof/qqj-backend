package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by king-ck on 2015/12/3.
 */
@Data
public class CustomerSkuProfitWrapper {

    private String sellerName;
    private String warehouseName;
    private Long restaurantId;
    private String restaurantName;

    private String accountReceivableType;
    private Long skuId;
    private String skuName;
    //    private String categoryName; //分类名称
    private String skuSingleUnit;

    private int quantity;
//    private BigDecimal avgCost;  //成本
//    private BigDecimal price;
    private BigDecimal salesAmount; //销售额
    private BigDecimal avgCostAmount;  //成本额
    private BigDecimal profitAmount;//毛利
    private BigDecimal profitRate; //毛利率
//    private Date createDate;

    public CustomerSkuProfitWrapper(String sellerName, String warehouseName, Long restaurantId, String restaurantName,
                                    Integer accountReceivableType, Long skuId, String skuName, String skuSingleUnit,
                                    int quantity, BigDecimal salesAmount, BigDecimal avgCostAmount) {
        this.sellerName = sellerName;
        this.warehouseName = warehouseName;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.skuId = skuId;
        this.skuName = skuName;
        this.skuSingleUnit = skuSingleUnit;
        this.quantity = quantity;
//        this.avgCost = avgCost;
//        this.price = price;
        this.accountReceivableType = AccountReceivableType.fromInt(accountReceivableType).getName();

        this.quantity=quantity;
        this.salesAmount=salesAmount;
        this.avgCostAmount=avgCostAmount;

        this.profitAmount = this.getSalesAmount().subtract(this.getAvgCostAmount());
        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO :
                this.profitAmount.divide(this.salesAmount.abs(), 4, BigDecimal.ROUND_CEILING);

//        if (accountReceivableType == AccountReceivableType.SELL.getValue()) {
//            this.quantity = this.getQuantity();
//
//        } else if (accountReceivableType == AccountReceivableType.RETURN.getValue()) {
//            this.quantity = this.getQuantity() * (-1);
//        }
//        this.salesAmount = this.getPrice().multiply(new BigDecimal(this.getQuantity()));
//        this.profitAmount = (this.getPrice().subtract(this.getAvgCost())).multiply(new BigDecimal(this.getQuantity()));
//
//        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO : this.profitAmount.divide(this.salesAmount, 4, BigDecimal.ROUND_CEILING);
    }

}
