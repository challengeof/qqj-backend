package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.wrapper.BlockWrapper;
import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import com.mishu.cgwy.profile.domain.Customer;
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
    private BlockWrapper block;

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
            adminUser.setGlobalAdmin(adminUserEntity.isGlobalAdmin());
        }

        this.createTime = customer.getCreateTime();
        this.block = new BlockWrapper(customer.getBlock());
    }
}
