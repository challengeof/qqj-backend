package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class AccountPayableWriteoff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 16, scale = 2)
    private BigDecimal writeOffAmount;

    @Temporal(TemporalType.DATE)
    private Date writeOffDate;

    @ManyToOne
    @JoinColumn(name = "writeOffer_id")
    private AdminUser writeOffer;

    private Date createDate;

    @Temporal(TemporalType.DATE)
    private Date cancelDate;

    @ManyToOne
    @JoinColumn(name = "canceler_id")
    private AdminUser canceler;

    private Date realCancelDate;

    private Short status;

    @ManyToOne
    @JoinColumn(name = "account_payable_id")
    private AccountPayable accountPayable;

}
