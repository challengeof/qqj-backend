package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class StockInItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int expectedQuantity;

    private int realQuantity;

    @ManyToOne
    @JoinColumn(name = "stock_in_id")
    private StockIn stockIn;

    @Column(precision = 16,scale = 6)
    private BigDecimal price;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 16,scale = 6)
    private BigDecimal salePrice;

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    @Transient
    private Date productionDate;

    public StockInItem clone () {
        StockInItem stockInItem = new StockInItem();
        stockInItem.setAvgCost(this.avgCost);
        stockInItem.setExpectedQuantity(this.expectedQuantity);
        stockInItem.setTaxRate(this.taxRate);
        stockInItem.setPrice(this.price);
        stockInItem.setRealQuantity(this.realQuantity);
        stockInItem.setSku(this.sku);
        stockInItem.setSalePrice(this.salePrice);
        return stockInItem;
    }

}
