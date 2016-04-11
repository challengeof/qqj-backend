package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;

import java.util.Date;

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

    private Long cityId;

    private AdminUserVo adminUser;
    private AdminUserVo devUser;    //销售开发人员


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
        this.cityId = customer.getCity().getId();

    }
}
