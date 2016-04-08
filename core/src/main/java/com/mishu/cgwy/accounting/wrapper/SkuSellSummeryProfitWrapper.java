package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by king-ck on 2015/12/8.
 */
@Data
public class SkuSellSummeryProfitWrapper {

    private Long skuId;
    private String skuName;
    private String accountReceivableType;
    private int capacityInBundle = 1; //转换率
    private String skuSingleUnit; //单位
    private int quantity;   //销量
    private String skuBundleUnit; //打包销量



    private BigDecimal bundleQuantity; //打包销量
    private BigDecimal salesAmount; //销售额
    private BigDecimal avgCostAmount;  //成本额
    private BigDecimal profitAmount;//毛利额
    private BigDecimal profitRate; //毛利率

//    private BigDecimal avgCost;  //成本价
//    private BigDecimal price;    //销售价

    public SkuSellSummeryProfitWrapper(Long skuId, String skuName,  Integer accountReceivableType,int capacityInBundle,String skuSingleUnit,
                                       int quantity, String skuBundleUnit, BigDecimal salesAmount, BigDecimal avgCostAmount ) {

        this.skuId = skuId;
        this.skuName = skuName;
        this.accountReceivableType = AccountReceivableType.fromInt(accountReceivableType).getName();;
        this.capacityInBundle = capacityInBundle;
        this.skuSingleUnit = skuSingleUnit;

        this.skuBundleUnit = skuBundleUnit;

        this.quantity=quantity;
        this.salesAmount=salesAmount;
        this.avgCostAmount=avgCostAmount;

        this.bundleQuantity = new BigDecimal(this.quantity).divide(new BigDecimal(capacityInBundle), 2, RoundingMode.HALF_UP);
        this.profitAmount = this.getSalesAmount().subtract(this.getAvgCostAmount());
        this.profitRate = this.salesAmount.doubleValue() == 0 ? BigDecimal.ZERO :
                this.profitAmount.divide(this.salesAmount.abs(), 4, BigDecimal.ROUND_CEILING);



    }
}
