package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.stock.wrapper.SellReturnReasonWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/2/15
 * Time: 2:08 PM
 */
@Data
public class OrderItemWrapper {
    private Long id;

    private Long orderId;

    private SimpleSkuWrapper sku;

    private BigDecimal price;

    private int singleQuantity;

    private int bundleQuantity;

    private int quantity;

    private BigDecimal totalPrice;

    private Date submitDate;

    private SimpleRestaurantWrapper restaurant;

    private OrderStatus orderStatus;

    private boolean bundle;
    private int countQuantity; //总购买数量
    private int sellCancelQuantity; //总取消数量
    private int sellReturnQuantity; //总退货数量

    private SimpleOrderWrapper order;

    private SpikeItemWrapper spikeItem;

    public OrderItemWrapper() {

    }

    public OrderItemWrapper(OrderItem orderItem) {
        id = orderItem.getId();
        orderId = orderItem.getOrder().getId();
        sku = new SimpleSkuWrapper(orderItem.getSku());
        price = orderItem.getPrice();
        singleQuantity = orderItem.getSingleQuantity();
        bundleQuantity = orderItem.getBundleQuantity();
        quantity = orderItem.isBundle() ? bundleQuantity : singleQuantity;
        totalPrice = orderItem.getTotalPrice();
        submitDate = orderItem.getOrder().getSubmitDate();

        // restaurant is null should only happen in uncommitted order
        if (orderItem.getOrder().getRestaurant() != null) {
            restaurant = new SimpleRestaurantWrapper(orderItem.getOrder().getRestaurant());
        }

        orderStatus = OrderStatus.fromInt(orderItem.getOrder().getStatus());
        bundle = orderItem.isBundle();
        countQuantity = orderItem.getCountQuantity();
        sellCancelQuantity = orderItem.getSellCancelQuantity();
        sellReturnQuantity = orderItem.getSellReturnQuantity();
        order = new SimpleOrderWrapper(orderItem.getOrder());

        if(null != this.spikeItem){
            this.spikeItem=new SpikeItemWrapper(orderItem.getSpikeItem(),null,null);
        }
    }

}
