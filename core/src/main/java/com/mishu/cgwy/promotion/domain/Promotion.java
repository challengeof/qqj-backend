package com.mishu.cgwy.promotion.domain;

import com.mishu.cgwy.organization.domain.Organization;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/28/15
 * Time: 3:57 PM
 */
@Entity
@Data
@EqualsAndHashCode(of = {"id"})
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    private String description;

    // MVEL : https://github.com/mvel/mvel
    private String rule;

    private BigDecimal discount = BigDecimal.ZERO;

    private PromotableItems promotableItems = null;

    private Integer promotionConstants;

    private Integer type;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Integer limitedQuantity;

    private int quantitySold;

    private Boolean enabled;

    @Column(length = 2000)
    private String ruleValue;
}
