package com.mishu.cgwy.salesPerformance.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
@Entity
@Data
public class RestaurantSalesPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private int orders;

    private BigDecimal salesAmount = BigDecimal.ZERO;

    private BigDecimal avgCostAmount = BigDecimal.ZERO;

    @Temporal(TemporalType.DATE)
    private Date date;
}
