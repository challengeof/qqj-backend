package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Temporal(TemporalType.DATE)
    private Date payDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    private Date createDate;

    private Short status;

    @ManyToOne
    @JoinColumn(name = "canceler_id")
    private AdminUser canceler;

    private Date cancelDate;

    private String remark;

    @ManyToOne
    @JoinColumn(name = "collection_payment_method_id")
    private CollectionPaymentMethod collectionPaymentMethod;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

}
