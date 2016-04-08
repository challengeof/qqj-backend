package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.wrapper.BlockWrapper;
import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 4/17/15
 * Time: 2:34 PM
 */
@Data
public class CustomerWrapper {
    private Long id;

    private String username;

    private String userNumber;

    private Date createTime;

    //暂时保留(兼容老数据)
    private ZoneWrapper zone;

    private BlockWrapper block;

    private Long cityId;

    private AdminUserVo adminUser;
    private AdminUserVo devUser;    //销售开发人员

    private List<RestaurantWrapper> restaurant;

//    private CustomerCreateModeEnum createMode;// 创建方式 CustomerCreateModeEnum


    public CustomerWrapper() {}

    public CustomerWrapper(Customer customer) {
        this.id = customer.getId();
        this.username = customer.getUsername();
        this.userNumber = customer.getUserNumber();
        if (customer.getAdminUser() != null) {
            AdminUser adminUserEntity = customer.getAdminUser();
            adminUser = new AdminUserVo();
            adminUser.setId(adminUserEntity.getId());
            adminUser.setUsername(adminUserEntity.getUsername());
            adminUser.setTelephone(adminUserEntity.getTelephone());
            adminUser.setEnabled(adminUserEntity.isEnabled());
            adminUser.setRealname(adminUserEntity.getRealname());
            adminUser.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }

        if(customer.getDevUser()!=null){
            AdminUser adminUserEntity = customer.getDevUser();
            devUser = new AdminUserVo();
            devUser.setId(adminUserEntity.getId());
            devUser.setUsername(adminUserEntity.getUsername());
            devUser.setTelephone(adminUserEntity.getTelephone());
            devUser.setEnabled(adminUserEntity.isEnabled());
            devUser.setRealname(adminUserEntity.getRealname());
            devUser.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }

        this.createTime = customer.getCreateTime();
        if(customer.getBlock() != null) {
            this.block = new BlockWrapper(customer.getBlock());
        }
        this.cityId = customer.getCity().getId();
        this.restaurant = RestaurantWrapper.getWrappers(customer.getRestaurant());


//        if(customer.getCreateMode()!=null) {
//            this.createMode = CustomerCreateModeEnum.fromInt(customer.getActiveType());
//        }

    }
}
