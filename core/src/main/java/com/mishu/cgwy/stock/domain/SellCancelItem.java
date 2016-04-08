package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
@Entity
public class SellCancelItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    /**
     * 单品单价  或  打包价/转化率
     */
    @Column(precision = 16, scale = 6)
    private BigDecimal price;

    private boolean bundle;

    @ManyToOne
    @JoinColumn(name = "sell_cancel_id")
    private SellCancel sellCancel;

    private Integer reason;

    private String memo = "";

    @Override
    public String toString() {
        return "SellCancelItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price=" + price +
                ", bundle=" + bundle +
                '}';
    }
}
