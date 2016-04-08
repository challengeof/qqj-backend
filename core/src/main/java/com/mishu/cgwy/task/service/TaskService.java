
package com.mishu.cgwy.task.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public void save(Task task) {
        taskRepository.save(task);
    }

    public List<Task> getTaskList(AdminUser adminUser) {
        return taskRepository.findBySubmitUserOrderByIdDesc(adminUser);
    }

    public Task getOne(Long taskId) {
        return taskRepository.getOne(taskId);
    }

    public List<Task> findBySubmitDateLessThan(Date date) {
        return taskRepository.findBySubmitDateLessThan(date);
    }

    public void delete(Long id) {
        taskRepository.delete(id);
    }
}


