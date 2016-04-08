package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by xingdong on 15/7/6.
 */
@Entity
@Data
public class Block {
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

    @Column(columnDefinition = "text")
    private String pointStr;

    @Transient
    public String getDisplayName() {
        return city.getName() + "-" + name;
    }
}
