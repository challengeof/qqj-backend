package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class StockAdjust {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private int status;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int quantity;

    private int adjustQuantity;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    private String shelfName;

    private String comment;

    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    private Date auditDate;

    @ManyToOne
    @JoinColumn(name = "auditor_id")
    private AdminUser auditor;

    @Version
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long version;

    @Override
    public String toString() {
        return "StockAdjust{" +
                "id=" + id +
                ", status=" + status +
                ", quantity=" + quantity +
                ", adjustQuantity=" + adjustQuantity +
                ", createDate=" + createDate +
                ", auditDate=" + auditDate +
                ", taxRate=" + taxRate +
                ", avgCost=" + avgCost +
                ", expirationDate=" + expirationDate +
                '}';
    }

}
