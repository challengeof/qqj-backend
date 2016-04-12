package com.qqj.profile.wrapper;

import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.vo.AdminUserVo;
import com.qqj.profile.domain.Customer;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 4/17/15
 * Time: 2:34 PM
 */
@Data
public class SimpleCustomerWrapper {
    private Long id;

    private String username;

    private String userNumber;

    private Date createTime;

    /*//暂时保留(兼容老数据)
    private ZoneWrapper zone;
*/

    private AdminUserVo adminUser;

    public SimpleCustomerWrapper() {

    }

    public SimpleCustomerWrapper(Customer customer) {
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
