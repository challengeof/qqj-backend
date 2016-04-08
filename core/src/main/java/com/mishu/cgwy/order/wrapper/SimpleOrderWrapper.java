package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.profile.wrapper.SimpleCustomerWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 4:28 PM
 */
@Data
public class SimpleOrderWrapper {
    private Long id;

    private BigDecimal total;

    private BigDecimal subTotal;

    private BigDecimal realTotal;

    private BigDecimal shipping;

    private OrderStatus status;

    private Date submitDate;

    private Date completeDate;

    private String memo;

    private SimpleCustomerWrapper customer;

    private SimpleRestaurantWrapper restaurant;

    private String orderNumber;
    
    private Long sequence;
    
    protected OrganizationVo organization;
    
    private boolean hasEvaluated;

    private AdminUserVo adminUser;

    private Date expectedArrivedDate;

    private BigDecimal totalWight = BigDecimal.ZERO;
    private BigDecimal totalVolume = BigDecimal.ZERO;
    private BigDecimal quantity = BigDecimal.ZERO;

    private List<SimpleOrderItemWrapper> orderItems = new ArrayList<>();

    private AdminUserVo adminOperator;

    private SimpleCustomerWrapper customerOperator;

    public SimpleOrderWrapper() {

    }

    public SimpleOrderWrapper(Order order) {
        id = order.getId();
        total = order.getTotal();
        subTotal = order.getSubTotal();
        realTotal = order.getRealTotal();
        shipping = order.getShipping();
        status = OrderStatus.fromInt(order.getStatus());
        submitDate = order.getSubmitDate();
        memo = order.getMemo();
        restaurant = new SimpleRestaurantWrapper(order.getRestaurant());
        orderNumber = String.valueOf(id);
        customer = new SimpleCustomerWrapper(order.getCustomer());
        sequence = order.getSequence();
        hasEvaluated = order.isHasEvaluated();

        Organization orderOrganization = order.getOrganization();
        if (orderOrganization != null) {
            organization = new OrganizationVo();
            organization.setId(orderOrganization.getId());
            organization.setName(orderOrganization.getName());
            organization.setCreateDate(orderOrganization.getCreateDate());
            organization.setEnabled(orderOrganization.isEnabled());
            organization.setTelephone(orderOrganization.getTelephone());
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
        completeDate = order.getCompleteDate();
        expectedArrivedDate = order.getExpectedArrivedDate();

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

        for (OrderItem orderItem : order.getOrderItems()) {

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
            totalVolume = totalVolume.add(orderItem.isBundle() ? bundleVolume : singleVolume);
            quantity = quantity = orderItem.isBundle() ? bundleQuantity : singleQuantity;

            if(sku.getBundleGross_wight() != null && orderItem.isBundle()){
                totalWight = totalWight.add(sku.getBundleGross_wight().multiply(bundleQuantity));
            }else if(sku.getSingleGross_wight() != null && !orderItem.isBundle()){
                totalWight = totalWight.add(sku.getSingleGross_wight().multiply(singleQuantity));
            }

        }
        totalVolume = new BigDecimal(totalVolume.floatValue() / 1000000).setScale(2,BigDecimal.ROUND_HALF_UP);
//        totalWight = new BigDecimal(totalWight.floatValue() / 1000000).setScale(2,BigDecimal.ROUND_HALF_UP);
    }
}
