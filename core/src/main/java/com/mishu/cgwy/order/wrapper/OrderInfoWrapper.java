package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.stock.wrapper.SellCancelWrapper;
import com.mishu.cgwy.stock.wrapper.SellReturnWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class OrderInfoWrapper {

    private Long orderId;
    private String orderType;
    private String orderStatus;
    private String submitName;
    private Date submitDate;
    private String memo;
    private String warehouseName;
    private String blockName;

    private Long restaurantId;
    private String restaurantName;
    private String registerPhone;
    private String receiver;
    private String telephone;
    private String address;
    private String sellerName;
    private Long stockOutId;

    private Date cutDate;
    private String cutOperator;
    private Date stockOutDate;
    private String stockOutOperator;
    private Date receiveDate;
    private String receiveOperator;

    private List<SimpleOrderItemWrapper> orderItems = new ArrayList<>();
    private List<PromotionWrapper> promotions = new ArrayList<>();
    private List<CustomerCouponWrapper> customerCoupons = new ArrayList<>();

    private List<SellReturnWrapper> sellReturns;
    private List<SellCancelWrapper> sellCancels;

    public OrderInfoWrapper() {
    }

    public OrderInfoWrapper(Order order) {

        orderId = order.getId();
        orderType = OrderType.find(order.getType(), OrderType.NOMAL).getDesc();
        orderStatus = OrderStatus.fromInt(order.getStatus()).getName();
        if (order.getAdminOperator() != null) {
            submitName = order.getAdminOperator().getRealname();
        }
        if (order.getCustomerOperator() != null) {
            submitName = order.getCustomerOperator().getUsername();
        }
        submitDate = order.getSubmitDate();
        memo = order.getMemo();
        warehouseName = order.getCustomer().getBlock().getWarehouse().getName();
        blockName = order.getCustomer().getBlock().getName();
        restaurantId = order.getRestaurant().getId();
        restaurantName = order.getRestaurant().getName();
        registerPhone = order.getCustomer().getTelephone();
        receiver = order.getRestaurant().getReceiver();
        telephone = order.getRestaurant().getTelephone();
        address = order.getRestaurant().getAddress().getAddress() + order.getRestaurant().getAddress().getStreetNumber();
        sellerName = order.getCustomer().getAdminUser().getRealname();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItems.add(new SimpleOrderItemWrapper(orderItem));
        }
        for (Promotion promotion : order.getPromotions()) {
            promotions.add(new PromotionWrapper(promotion));
        }
        for (CustomerCoupon customerCoupon : order.getCustomerCoupons()) {
            customerCoupons.add(new CustomerCouponWrapper(customerCoupon));
        }
    }

}
