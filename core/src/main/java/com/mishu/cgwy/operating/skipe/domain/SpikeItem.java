package com.mishu.cgwy.operating.skipe.domain;

import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by king-ck on 2016/1/7.
 */
@Entity
@Data
@EqualsAndHashCode(of = {"id"})
public class SpikeItem implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spike_id")
    private Spike spike;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private BigDecimal originalPrice;//商品原价

    private BigDecimal price; //秒杀价格

    private Integer num; //总数量

    private Integer perMaxNum; //每个客户限制购买数量

    private boolean bundle; //是否打包

    private int takeNum; //已参与数量

    @Override
    public String toString() {
        return "SpikeItem{" +
                "id=" + id +
                ", originalPrice"+ originalPrice+
                ", price=" + price +
                ", num=" + num +
                ", perMaxNum=" + perMaxNum +
                ", bundle=" + bundle +
                ", takeNum=" + takeNum +
                '}';
    }
}
