package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:56 PM
 */
@Entity
@Data
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Deprecated
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Transient
    public String getDisplayName() {
        return city.getName() + "-" + name;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
