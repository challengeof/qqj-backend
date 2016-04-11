package com.mishu.cgwy.schedule.facade;

import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.service.TaskService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bowen on 15/11/10.
 */
@Service
public class ScheduleFacade {
    private static Logger logger = LoggerFactory.getLogger(ScheduleFacade.class);

    @Autowired
    private TaskService taskService;

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();
    @Autowired(required=false)
    private JavaMailSender mailSender;


    public void deleteExcels() {

        Date dateToDelete = DateUtils.truncate(DateUtils.addDays(new Date(), -2), Calendar.DATE);
        List<Task> taskList = taskService.findBySubmitDateLessThan(dateToDelete);

        for (Task task : taskList) {
            taskService.delete(task.getId());
        }

        File excelFolder = new File(ExportExcelUtils.excelFolderName);
        if (excelFolder.exists() && excelFolder.isDirectory()) {
            File[] files = excelFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < dateToDelete.getTime()) {
                        file.delete();
                    }
                }
            }
        }
    }


}
