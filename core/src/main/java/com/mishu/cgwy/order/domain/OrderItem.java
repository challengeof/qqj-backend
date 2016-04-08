package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:46 PM
 */
@Entity
@Table(name = "order_item", indexes = {@Index(name = "ORDERITEM_ORDER_INDEX", columnList = "ORDER_ID")})
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "spike_item")
    private SpikeItem spikeItem;  //关联秒杀商品

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    private boolean bundle;

    private int singleQuantity; //单品数量
    private int bundleQuantity; //打包数量

    private int countQuantity; //总数量(单品)

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    private int sellCancelQuantity; //总取消数量
    private int sellReturnQuantity; //总退货数量

    @Column(precision = 19, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", price=" + price +
                ", singleQuantity=" + singleQuantity +
                ", bundleQuantity=" + bundleQuantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
