package com.mishu.cgwy.task.vo;

import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.enumeration.TaskStatus;
import com.mishu.cgwy.task.enumeration.TaskType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Getter
@Setter
public class TaskVo {
    private Long id;

    private String submitUser;

    private String submitDate;

    private BigDecimal timeCost;//in seconds

    private TaskStatus status;

    private TaskType type;

    private String description;

    private String remark;
}
