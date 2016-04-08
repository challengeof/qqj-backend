package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * User: xudong
 * Date: 5/20/15
 * Time: 4:16 PM
 */
@Entity
@Data
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTime;
}
