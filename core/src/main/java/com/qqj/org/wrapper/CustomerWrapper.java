package com.qqj.org.wrapper;

import com.qqj.org.domain.Customer;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerWrapper {

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

    private CustomerStatus status;

    private TeamWrapper team;

    private boolean founder;

    private StockWrapper stock;

    public CustomerWrapper(Customer customer) {
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
            this.status = CustomerStatus.get(customer.getStatus());
            this.team = new TeamWrapper(customer.getTeam());
            this.founder = customer.isFounder();
            this.stock = new StockWrapper(customer.getStock());
        }
    }
}
