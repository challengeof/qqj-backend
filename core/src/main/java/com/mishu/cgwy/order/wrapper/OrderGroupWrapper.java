package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.wrapper.WarehouseWrapper;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/6/15
 * Time: 12:52 PM
 */
@Data
public class OrderGroupWrapper {
    private Long id;

    private List<OrderWrapper> members = new ArrayList<>();

    private City city;

    private Depot depot;

    private int lock;

    private AdminUserVo tracker;

    private String name;

//    private Date expectedArrivedDate;

    private BigDecimal sumOfTotal = BigDecimal.ZERO;

    private BigDecimal sumOfSubTotal = BigDecimal.ZERO;

    private OrganizationVo organization;

    public OrderGroupWrapper() {

    }

    public OrderGroupWrapper(OrderGroup orderGroup) {

        id = orderGroup.getId();
        city = orderGroup.getCity();
        depot = orderGroup.getDepot();

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
        name = orderGroup.getName();
        for (Order member : orderGroup.getMembers()) {
            if(lock == 0 && (member.getStatus() == OrderStatus.SHIPPING.getValue()
                    || member.getStatus() == OrderStatus.COMPLETED.getValue()
                    || member.getStatus() == OrderStatus.RETURNED.getValue())){
                lock = 1; //对包含以上状态的订单的订单包加锁,不允许删除和编辑订单包
            }

            members.add(new OrderWrapper(member));
            sumOfTotal = sumOfTotal.add(member.getTotal());
            sumOfSubTotal = sumOfSubTotal.add(member.getSubTotal());
        }

        Organization orderGroupOrganization = orderGroup.getOrganization();
        organization = new OrganizationVo();
        organization.setId(orderGroupOrganization.getId());
        organization.setName(orderGroupOrganization.getName());
        organization.setCreateDate(orderGroupOrganization.getCreateDate());
        organization.setEnabled(orderGroupOrganization.isEnabled());
        organization.setTelephone(orderGroupOrganization.getTelephone());
    }
}
