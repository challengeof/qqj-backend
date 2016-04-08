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
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int stock;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    /**
     * 用于调拨的在途库存
     */
    @ManyToOne
    @JoinColumn(name = "stock_in_id")
    private StockIn stockIn;

    /**
     * 用于出库占库存
     */
    @ManyToOne
    @JoinColumn(name = "stock_out_id")
    private StockOut stockOut;

    /**
     * 用于调整占库存
     */
    @ManyToOne
    @JoinColumn(name = "stock_adjust_id")
    private StockAdjust stockAdjust;

    @Version
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long version;

    public Stock clone () {
        Stock stock = new Stock();
        stock.setDepot(this.depot);
        stock.setStock(this.stock);
        stock.setTaxRate(this.taxRate);
        stock.setShelf(this.shelf);
        stock.setExpirationDate(this.expirationDate);
        stock.setSku(this.sku);

        return stock;
    }

}
