package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by wangguodong on 15/9/14.
 */
@Entity
@Getter
@Setter
public class PurchaseOrderItemSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private Short status;
}
