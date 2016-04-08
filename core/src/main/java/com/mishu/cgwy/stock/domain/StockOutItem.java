package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class StockOutItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int expectedQuantity;

    private int realQuantity;

    private int receiveQuantity;

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 16, scale = 6)
    private BigDecimal price;

    //采退用，采购单价
    @Column(precision = 16, scale = 6)
    private BigDecimal purchasePrice;

    private boolean bundle;

    private int status;

    @ManyToOne
    @JoinColumn(name = "stock_out_id")
    private StockOut stockOut;

    @ManyToOne
    @JoinColumn(name = "transfer_out_id")
    private StockOut transferOut;

    public StockOutItem clone () {
        StockOutItem stockOutItem = new StockOutItem();
        stockOutItem.setAvgCost(this.avgCost);
        stockOutItem.setExpectedQuantity(this.expectedQuantity);
        stockOutItem.setTaxRate(this.taxRate);
        stockOutItem.setPrice(this.price);
        stockOutItem.setRealQuantity(this.realQuantity);
        stockOutItem.setReceiveQuantity(this.receiveQuantity);
        stockOutItem.setStatus(this.status);
        stockOutItem.setSku(this.sku);
        stockOutItem.setBundle(this.bundle);
        stockOutItem.setPurchasePrice(this.purchasePrice);

        return stockOutItem;
    }

}
