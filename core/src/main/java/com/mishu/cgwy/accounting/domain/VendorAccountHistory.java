package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.inventory.domain.Vendor;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class VendorAccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(precision = 16, scale = 2)
    private BigDecimal unWriteoffAmount;

    private Date createDate;

    @Temporal(TemporalType.DATE)
    private Date accountDate;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "account_payable_id")
    private AccountPayable accountPayable;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "account_payable_writeoff_id")
    private AccountPayableWriteoff accountPayableWriteoff;

    private Short type;
}
