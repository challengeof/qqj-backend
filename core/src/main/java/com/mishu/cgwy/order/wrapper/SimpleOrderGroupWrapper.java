package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 4/6/15
 * Time: 12:52 PM
 */
@Data
public class SimpleOrderGroupWrapper {

    private Long id;

    private int memberSize;

    private int customerSize; //下单总用户数

    private AdminUserVo tracker;

    private String carExpenses;

    private String carSource;

    private String carTaxingPoint;

    private String name;

//    private Date expectedArrivedDate;

    private BigDecimal sumOfTotal = BigDecimal.ZERO;
    private BigDecimal sumOfSubTotal = BigDecimal.ZERO;
    private BigDecimal totalWight = BigDecimal.ZERO;
    private BigDecimal totalVolume = BigDecimal.ZERO;
    private BigDecimal quantity = BigDecimal.ZERO;

    private OrganizationVo organization;

    public SimpleOrderGroupWrapper() {

    }

    public SimpleOrderGroupWrapper(OrderGroup orderGroup) {
        id = orderGroup.getId();
        if (orderGroup.getTracker() != null) {
            AdminUser adminUserEntity = orderGroup.getTracker();
            tracker = new AdminUserVo();
            tracker.setId(adminUserEntity.getId());
            tracker.setUsername(adminUserEntity.getUsername());
            tracker.setTelephone(adminUserEntity.getTelephone());
            tracker.setEnabled(adminUserEntity.isEnabled());
            tracker.setRealname(adminUserEntity.getRealname());
            tracker.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }
//        expectedArrivedDate = orderGroup.getExpectedArrivedDate();
        name = orderGroup.getName();
        Map<Long,Integer> customerTotalMap = new HashMap<Long,Integer>(); //下单商户总数
        for (Order member : orderGroup.getMembers()) {
//            members.add(new SimpleOrderWrapper(member));
            if(customerTotalMap.get(member.getCustomer().getId()) == null){
                customerSize++;
                customerTotalMap.put(member.getCustomer().getId() , 1);
            }

            memberSize ++;
            sumOfTotal = sumOfTotal.add(member.getTotal());
            sumOfSubTotal = sumOfSubTotal.add(member.getSubTotal());
            OrderWrapper orderWrapper = new OrderWrapper(member);
            totalWight = totalWight.add(orderWrapper.getTotalWight());
            totalVolume = totalVolume.add(orderWrapper.getTotalVolume());
            quantity = quantity.add(orderWrapper.getQuantity());
        }

        Organization orderGroupOrganization = orderGroup.getOrganization();
        if (orderGroupOrganization != null) {
            organization = new OrganizationVo();
            organization.setId(orderGroupOrganization.getId());
            organization.setName(orderGroupOrganization.getName());
            organization.setCreateDate(orderGroupOrganization.getCreateDate());
            organization.setEnabled(orderGroupOrganization.isEnabled());
            organization.setTelephone(orderGroupOrganization.getTelephone());
        }
    }
}
