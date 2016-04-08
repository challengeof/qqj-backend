package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/9/14.
 */
@Entity
@Data
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private Integer purchaseQuantity;

    private Integer needQuantity;

    private Integer returnQuantity;

    @Column(precision = 16, scale = 6)
    private BigDecimal price;

    private Short status;

    @Column(precision = 16, scale = 2)
    private BigDecimal rate;

    public PurchaseOrderItem clone () {
        PurchaseOrderItem poi = new PurchaseOrderItem();
        poi.setStatus(this.status);
        poi.setSku(this.sku);
        poi.setNeedQuantity(this.needQuantity);
        poi.setPrice(this.price);
        poi.setPurchaseQuantity(this.purchaseQuantity);
        poi.setRate(this.rate);
        return poi;
    }
}
