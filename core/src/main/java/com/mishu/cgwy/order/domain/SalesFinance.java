package com.mishu.cgwy.order.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;

@Entity
@Data
public class SalesFinance {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Temporal(TemporalType.DATE)
    private Date statisticsDate;
	
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;
	
    private Long orderQuantity = 0l;

    private BigDecimal salesUnitPrice = BigDecimal.ZERO;

    private Long returnedQuantity = 0l;

    private Integer stock = 0;
    
    private Long stockUsed = 0l;
    
    private BigDecimal avgPrice = BigDecimal.ZERO;
    
    private Long purchaseUsed = 0l;

    private BigDecimal purchasePrice = BigDecimal.ZERO;
    
    private BigDecimal salesTotal = BigDecimal.ZERO;
    
    private BigDecimal spendingTotal = BigDecimal.ZERO;
    
    @Column(name="gross_margins", precision=10, scale=5)
    private BigDecimal grossMargins = BigDecimal.ZERO;
}
