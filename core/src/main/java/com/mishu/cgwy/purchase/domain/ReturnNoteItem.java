package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/10/10.
 */
@Entity
@Data
public class ReturnNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "return_note_id")
    private ReturnNote returnNote;

    private Integer returnQuantity;

    @Column(precision = 16, scale = 6)
    private BigDecimal returnPrice;

    @ManyToOne
    @JoinColumn(name = "purchase_order_item_id")
    private PurchaseOrderItem purchaseOrderItem;
}
