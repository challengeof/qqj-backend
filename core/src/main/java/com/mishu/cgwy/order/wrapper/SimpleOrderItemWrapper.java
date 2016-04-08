package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:08 PM
 */
@Data
public class SimpleOrderItemWrapper {
    private Long id;

    private Long orderId;

    private SkuWrapper sku;

    private BigDecimal price;

    private boolean bundle;

    private int quantity;

    private String unit;

    private int singleQuantity;

    private int bundleQuantity;

    private int countQuantity; //总购买数量
    private int sellCancelQuantity; //总取消数量
    private int sellReturnQuantity; //总退货数量

    private BigDecimal totalPrice;

    private SpikeItemWrapper spikeItem;

    public SimpleOrderItemWrapper() {

    }

    public SimpleOrderItemWrapper(OrderItem orderItem) {
        id = orderItem.getId();
        orderId = orderItem.getOrder().getId();
        sku = new SkuWrapper(orderItem.getSku());
        price = orderItem.getPrice();
        singleQuantity = orderItem.getSingleQuantity();
        bundleQuantity = orderItem.getBundleQuantity();
        quantity = orderItem.isBundle() ? bundleQuantity : singleQuantity;
        unit = orderItem.isBundle() ? orderItem.getSku().getBundleUnit() : orderItem.getSku().getSingleUnit();
        totalPrice = orderItem.getTotalPrice();
        bundle = orderItem.isBundle();
        countQuantity = orderItem.getCountQuantity();
        sellCancelQuantity = orderItem.getSellCancelQuantity();
        sellReturnQuantity = orderItem.getSellReturnQuantity();

        if(null!=orderItem.getSpikeItem()){
            this.spikeItem = new SpikeItemWrapper(orderItem.getSpikeItem(), SpikeActivityState.parseSpikeActivity(orderItem.getSpikeItem().getSpike()));
        }
    }

}
