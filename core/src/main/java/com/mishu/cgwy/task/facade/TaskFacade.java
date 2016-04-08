package com.mishu.cgwy.task.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.enumeration.TaskStatus;
import com.mishu.cgwy.task.enumeration.TaskType;
import com.mishu.cgwy.task.service.TaskService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.task.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskFacade {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static BigDecimal convert = new BigDecimal(1000);

    @Autowired
    private TaskService taskService;

    public List<TaskVo> getTaskList(AdminUser adminUser) {
        List<Task> taskList = taskService.getTaskList(adminUser);

        List<TaskVo> taskVoList = new ArrayList<>();
        for (Task task : taskList) {
            TaskVo taskVo = new TaskVo();
            taskVo.setId(task.getId());
            taskVo.setSubmitUser(task.getSubmitUser().getRealname());
            taskVo.setSubmitDate(df.format(task.getSubmitDate()));
            taskVo.setTimeCost(new BigDecimal(task.getTimeCost()).divide(convert).setScale(2, RoundingMode.HALF_UP));
            taskVo.setStatus(TaskStatus.get(task.getStatus()));
            taskVo.setType(TaskType.get(task.getType()));
            taskVo.setDescription(task.getDescription());
            taskVo.setRemark(task.getRemark());
            taskVoList.add(taskVo);
        }
        return taskVoList;
    }

    public HttpEntity<byte[]> download(Long taskId) {
        Task task = taskService.getOne(taskId);
        return ExportExcelUtils.getHttpEntityXlsx(task.getResult());
    }
}

