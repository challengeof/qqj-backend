package com.mishu.cgwy.task.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
public class TaskResult {
    private String result;
    private String remark;
}
