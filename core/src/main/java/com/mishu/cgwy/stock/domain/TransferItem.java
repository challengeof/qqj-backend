package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;

/**
 * User: Admin
 * Date: 9/17/15
 * Time: 9:23 AM
 */
@Entity
@Data
public class TransferItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;


}
