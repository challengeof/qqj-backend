package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:46 PM
 */
@Deprecated
@Entity
@Data
//以后使用SellReturn表
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private boolean bundle;

    private int singleQuantity;
    private int bundleQuantity;

    private int countQuantity;

    @Column(name = "price", precision = 19, scale = 5)
    private BigDecimal price;

    @Column(name = "total_price", precision = 19, scale = 5)
    private BigDecimal totalPrice;
    
    private Integer type;

    private Date submitDate;

    private Integer reason;
}
