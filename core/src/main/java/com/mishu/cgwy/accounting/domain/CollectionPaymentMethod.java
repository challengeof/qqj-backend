package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.common.domain.City;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class CollectionPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private boolean cash = false;

    private boolean valid = true;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
