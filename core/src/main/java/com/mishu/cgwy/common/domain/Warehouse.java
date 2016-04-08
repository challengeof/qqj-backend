package com.mishu.cgwy.common.domain;

import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:34 PM
 */
@Entity
@Data
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Transient
    public String getDisplayName() {
        return city.getName() + "-" + name;
    }

    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private boolean active;
}

