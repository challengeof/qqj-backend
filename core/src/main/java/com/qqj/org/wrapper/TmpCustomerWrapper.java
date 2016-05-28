package com.qqj.org.wrapper;

import com.qqj.org.domain.PendingApprovalCustomer;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import lombok.Data;

import java.util.Date;

@Data
public class TmpCustomerWrapper {

    private Long id;

    private String name;

    private String certificateNumber;

    private CustomerWrapper parent;

    private CustomerLevel level;

    private String username;

    private String telephone;

    private String address;

    private Date createTime;

    private CustomerWrapper creator;

    private CustomerAuditStatus status;

    private TeamWrapper team;

    public TmpCustomerWrapper(PendingApprovalCustomer customer) {
        if (customer != null) {
            this.id = customer.getId();
            this.name = customer.getName();
            this.certificateNumber = customer.getCertificateNumber();
            this.parent = new CustomerWrapper(customer.getParent());
            this.level = CustomerLevel.get(customer.getLevel());
            this.username = customer.getUsername();
            this.telephone = customer.getTelephone();
            this.address = customer.getAddress();
            this.createTime = customer.getCreateTime();
            this.creator = new CustomerWrapper(customer.getParent());
            this.status = CustomerAuditStatus.get(customer.getStatus());
            this.team = new TeamWrapper(customer.getTeam());
        }
    }
}
