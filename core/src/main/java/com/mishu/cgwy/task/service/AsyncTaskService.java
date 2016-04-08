
package com.mishu.cgwy.task.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.enumeration.TaskStatus;
import com.mishu.cgwy.task.enumeration.TaskType;
import com.mishu.cgwy.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AsyncTaskService {

    @Autowired
    private TaskService taskService;

    private ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);

    public void export(Request request, AdminUser adminUser, String desc, AsyncTask export) throws Exception {
        Task task = new Task();
        task.setSubmitUser(adminUser);
        task.setSubmitDate(new Date());
        task.setTaskCondition(mapper.writeValueAsString(request));
        task.setStatus(TaskStatus.EXECUTION.getValue());
        task.setType(TaskType.EXCELEXPORT.getValue());
        task.setDescription(desc);
        taskService.save(task);

        export.setAsyncTaskService(this);
        export.setTask(task);
        export.start();
    }

    public void complete(Task task) {
        taskService.save(task);
    }

    public void excelImport(AdminUser adminUser, String desc, AsyncTask export) throws Exception {
        Task task = new Task();
        task.setSubmitUser(adminUser);
        task.setSubmitDate(new Date());
        task.setStatus(TaskStatus.EXECUTION.getValue());
        task.setType(TaskType.EXCELIMPORT.getValue());
        task.setDescription(desc);
        taskService.save(task);

        export.setAsyncTaskService(this);
        export.setTask(task);
        export.start();
    }
}


