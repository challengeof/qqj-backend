package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.order.domain.Refund;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import lombok.Data;

import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 4/19/15
 * Time: 10:22 PM
 */
@Data
public class RefundWrapper {
    private Long id;

    private SimpleOrderWrapper order;

    private SimpleSkuWrapper sku;

    private int quantity;

    private int singleQuantity;
    private int bundleQuantity;
    private int countQuantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    public RefundWrapper() {

    }

    public RefundWrapper(Refund refund) {
        id = refund.getId();
        order = new SimpleOrderWrapper(refund.getOrder());
        sku = new SimpleSkuWrapper(refund.getSku());
        singleQuantity = refund.getSingleQuantity();
        bundleQuantity = refund.getBundleQuantity();
        countQuantity = refund.getCountQuantity();
        price = refund.getPrice();
        totalPrice = refund.getTotalPrice();
    }

}
