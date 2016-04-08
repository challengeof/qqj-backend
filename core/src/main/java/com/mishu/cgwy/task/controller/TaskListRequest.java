package com.mishu.cgwy.task.controller;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class TaskListRequest {
    private int page = 0;

    private int pageSize = 100;
}
