package com.qqj.org.wrapper;

import com.qqj.admin.vo.AdminUserVo;
import com.qqj.org.domain.Customer;
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
        this.createTime = customer.getCreateTime();
    }
}
