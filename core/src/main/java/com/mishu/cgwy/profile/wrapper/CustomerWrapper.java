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
        if (customer.getAdminUser() != null) {
            AdminUser adminUserEntity = customer.getAdminUser();
            adminUser = new AdminUserVo();
            adminUser.setId(adminUserEntity.getId());
            adminUser.setUsername(adminUserEntity.getUsername());
            adminUser.setTelephone(adminUserEntity.getTelephone());
            adminUser.setEnabled(adminUserEntity.isEnabled());
            adminUser.setRealname(adminUserEntity.getRealname());
        }
        this.createTime = customer.getCreateTime();
    }
}
