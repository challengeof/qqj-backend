package com.qqj.org.wrapper;

import com.qqj.org.domain.RegisterTask;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterTaskWrapper {

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

    public RegisterTaskWrapper(RegisterTask task) {
        if (task != null) {
            this.id = task.getId();
            this.name = task.getName();
            this.certificateNumber = task.getCertificateNumber();
            this.parent = new CustomerWrapper(task.getParent());
            this.level = CustomerLevel.get(task.getLevel());
            this.username = task.getUsername();
            this.telephone = task.getTelephone();
            this.address = task.getAddress();
            this.createTime = task.getCreateTime();
            this.creator = new CustomerWrapper(task.getParent());
            this.status = CustomerAuditStatus.get(task.getStatus());
            this.team = new TeamWrapper(task.getTeam());
        }
    }
}
