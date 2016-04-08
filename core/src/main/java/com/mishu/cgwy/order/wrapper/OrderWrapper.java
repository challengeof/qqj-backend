package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.Refund;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.profile.wrapper.SimpleCustomerWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.stock.wrapper.SellCancelItemWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnItemWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 4:28 PM
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class OrderWrapper {
    private Long id;

    private BigDecimal total;

    //--------计算订单下商品的长、宽、高、重量总和 -------------
    private BigDecimal totalWight = BigDecimal.ZERO;
    private BigDecimal totalVolume = BigDecimal.ZERO;
    private BigDecimal quantity = BigDecimal.ZERO;

    private BigDecimal subTotal;

    private BigDecimal realTotal;

    private BigDecimal shipping;

    private SimpleCustomerWrapper customer;

    private OrderStatus status;

    private Date submitDate;

    private String memo;

    private SimpleRestaurantWrapper restaurant;

    private String orderNumber;
    
    private Date expectedArrivedDate;

    private List<SimpleOrderItemWrapper> orderItems = new ArrayList<>();

    private List<SimpleSellReturnItemWrapper> sellReturnItems = new ArrayList<>();

    private boolean havaUnFinishedSellCancel = false;

    private Long sellCancelId;

    private List<SellCancelItemWrapper> sellCancelItems = new ArrayList<>();

    private Fulfillment fulfillment;

    private Set<PromotionWrapper> promotions = new HashSet<>();
    
    private boolean hasEvaluated;

    private Set<CustomerCouponWrapper> customerCoupons = new HashSet<>();

    private AdminUserVo adminUser;

    private AdminUserVo adminOperator;

    private SimpleCustomerWrapper customerOperator;

    @Deprecated
    private AdminUserVo tracker;

    public OrderWrapper() {

    }

    public OrderWrapper(Order order) {
        id = order.getId();
        subTotal = order.getSubTotal();
        total = order.getTotal();
        realTotal = order.getRealTotal();
        shipping = order.getShipping();
        customer = new SimpleCustomerWrapper(order.getCustomer());
        status = OrderStatus.fromInt(order.getStatus());
        submitDate = order.getSubmitDate();
        memo = order.getMemo();
        hasEvaluated = order.isHasEvaluated();
        if (order.getAdminOperator() != null) {
            AdminUser adminUserEntity = order.getAdminOperator();
            adminOperator = new AdminUserVo();
            adminOperator.setId(adminUserEntity.getId());
            adminOperator.setUsername(adminUserEntity.getUsername());
            adminOperator.setTelephone(adminUserEntity.getTelephone());
            adminOperator.setEnabled(adminUserEntity.isEnabled());
            adminOperator.setRealname(adminUserEntity.getRealname());
            adminOperator.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }
        if (order.getCustomerOperator() != null) {
            customerOperator = new SimpleCustomerWrapper(order.getCustomerOperator());
        }

        // restaurant is null should only happen in uncommitted order
        if (order.getRestaurant() != null) {
            restaurant = new SimpleRestaurantWrapper(order.getRestaurant());
        }

        expectedArrivedDate = order.getExpectedArrivedDate();

        for (OrderItem orderItem : order.getOrderItems()) {
            orderItems.add(new SimpleOrderItemWrapper(orderItem));

            //--------计算订单下商品的长、宽、高、重量总和 -------------
            Sku sku = orderItem.getSku();
            BigDecimal bundleQuantity = new BigDecimal(orderItem.getBundleQuantity());
            BigDecimal singleQuantity = new BigDecimal(orderItem.getSingleQuantity());
            BigDecimal bundleVolume = BigDecimal.ZERO;
            BigDecimal singleVolume = BigDecimal.ZERO;

            if(sku.getBundleLong() != null && sku.getBundleWidth() != null && sku.getBundleHeight() != null)
                bundleVolume = sku.getBundleLong().multiply(sku.getBundleWidth()).multiply(sku.getBundleHeight()).multiply(bundleQuantity);
            if(sku.getSingleLong() != null && sku.getSingleWidth() != null && sku.getSingleHeight() != null)
                singleVolume = sku.getSingleLong().multiply(sku.getSingleWidth()).multiply(sku.getSingleHeight()).multiply(singleQuantity);
            totalVolume = totalVolume.add(orderItem.isBundle() ? bundleVolume : singleVolume); //立方厘米换算成立方米 1000000

            quantity = quantity.add(orderItem.isBundle() ? bundleQuantity : singleQuantity);

            if(sku.getBundleGross_wight() != null && orderItem.isBundle()){
                totalWight = totalWight.add(sku.getBundleGross_wight().multiply(bundleQuantity));
            }else if(sku.getSingleGross_wight() != null && !orderItem.isBundle()){
                totalWight = totalWight.add(sku.getSingleGross_wight().multiply(singleQuantity));
            }
        }

        totalVolume = new BigDecimal(totalVolume.floatValue() / 1000000).setScale(2,BigDecimal.ROUND_HALF_UP);
//        totalWight = new BigDecimal(totalWight.floatValue() / 1000000).setScale(2,BigDecimal.ROUND_HALF_UP);
        for (Promotion promotion : order.getPromotions()) {
            promotions.add(new PromotionWrapper(promotion));
        }

        for (CustomerCoupon customerCoupon : order.getCustomerCoupons()) {
            customerCoupons.add(new CustomerCouponWrapper(customerCoupon));
        }

        if (order.getAdminUser() != null) {
            AdminUser adminUserEntity = order.getAdminUser();
            adminUser = new AdminUserVo();
            adminUser.setId(adminUserEntity.getId());
            adminUser.setUsername(adminUserEntity.getUsername());
            adminUser.setTelephone(adminUserEntity.getTelephone());
            adminUser.setEnabled(adminUserEntity.isEnabled());
            adminUser.setRealname(adminUserEntity.getRealname());
            adminUser.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }
    }

}
