package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class RestaurantAccount {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private Restaurant restaurant;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(precision = 16, scale = 2)
    private BigDecimal unWriteoffAmount;
}
