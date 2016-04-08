package com.mishu.cgwy.salesPerformance.domain;

import com.mishu.cgwy.common.domain.Block;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
@Entity
@Data
public class BlockSalesPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private Block block;

    private int newCustomers;

    private int orders;

    private BigDecimal salesAmount = BigDecimal.ZERO;

    private BigDecimal avgCostAmount = BigDecimal.ZERO;

    @Temporal(TemporalType.DATE)
    private Date date;
}
