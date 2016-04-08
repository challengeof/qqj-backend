package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by wangguodong on 15/9/15.
 */
@Entity
@Data
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class Depot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean isMain = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Embedded
    private Wgs84Point wgs84Point;
}
