package com.mishu.cgwy.promotion.domain;

import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: xudong
 * Date: 4/29/15
 * Time: 1:30 AM
 */
@Embeddable
@Data
public class PromotableItems {
    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private boolean bundle;

    private int quantity;

}
