package com.mishu.cgwy.vendor.domain;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/12/30.
 */
@Entity
@Getter
@Setter
public class VendorOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private Integer quantityNeed;

    private Integer quantityReady;
}
