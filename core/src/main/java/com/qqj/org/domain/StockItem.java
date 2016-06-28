package com.qqj.org.domain;

import com.qqj.product.domain.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;


}
