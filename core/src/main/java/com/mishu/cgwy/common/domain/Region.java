package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:56 PM
 */
@Entity
@Data
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "region")
    private List<Zone> zones = new ArrayList<Zone>();

    @Transient
    public String getDisplayName() {
        return city.getName() + "-" + name;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
