package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class Collectionment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Temporal(TemporalType.DATE)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    private Date realCreateDate;

    private boolean valid = true;

    @ManyToOne
    @JoinColumn(name = "canceler_id")
    private AdminUser canceler;

    private Date cancelDate;

    @ManyToOne
    @JoinColumn(name = "collection_payment_method_id")
    private CollectionPaymentMethod collectionPaymentMethod;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

}
