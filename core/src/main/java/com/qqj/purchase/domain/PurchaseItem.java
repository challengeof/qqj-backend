package com.qqj.purchase.domain;

import com.qqj.product.domain.Product;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
