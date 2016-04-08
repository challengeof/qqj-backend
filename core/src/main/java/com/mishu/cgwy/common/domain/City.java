package com.mishu.cgwy.common.domain;

import com.mishu.cgwy.redis.RedisCache;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Cache;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:56 PM
 */
@Entity
@Data
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Override
    public boolean equals(Object city){
        return ((City)city).getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().intValue();
    }

}
