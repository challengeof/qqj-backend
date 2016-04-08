package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.order.wrapper.CustomerCouponWrapper;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleCustomerWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutItem;
import com.mishu.cgwy.stock.domain.StockOutItemStatus;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class StockOutOrderWrapper {
    private Long id;

    private Long cityId;

    private BigDecimal total;

    private SimpleCustomerWrapper customer;

    private Date submitDate;

    private String memo;

    private StockOutStatus status;

    private SimpleRestaurantWrapper restaurant;

    private List<StockOutOrderItemWrapper> orderItems = new ArrayList<>();

    private Set<PromotionWrapper> promotions = new HashSet<>();

    private Set<CustomerCouponWrapper> customerCoupons = new HashSet<>();

    public StockOutOrderWrapper() {

    }

    public StockOutOrderWrapper(StockOut stockOut) {
        id = stockOut.getId();
        cityId = stockOut.getDepot().getCity().getId();
        total = stockOut.getAmount();
        customer = new SimpleCustomerWrapper(stockOut.getOrder().getCustomer());
        submitDate = stockOut.getOrder().getSubmitDate();
        memo = stockOut.getOrder().getMemo();
        status = StockOutStatus.fromInt(stockOut.getStatus());
        if (stockOut.getOrder().getRestaurant() != null) {
            restaurant = new SimpleRestaurantWrapper(stockOut.getOrder().getRestaurant());
        }
        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                orderItems.add(new StockOutOrderItemWrapper(stockOutItem));
            }
        }

        for (Promotion promotion : stockOut.getOrder().getPromotions()) {
            promotions.add(new PromotionWrapper(promotion));
        }

        for (CustomerCoupon customerCoupon : stockOut.getOrder().getCustomerCoupons()) {
            customerCoupons.add(new CustomerCouponWrapper(customerCoupon));
        }
    }
}
