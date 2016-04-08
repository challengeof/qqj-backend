package com.mishu.cgwy.task.service;

import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.enumeration.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangguodong on 16/1/15.
 */
public abstract class AsyncTask {

    private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    private Task task;

    private AsyncTaskService asyncTaskService;

    public void setTask(Task task) {
        this.task = task;
    }

    public void setAsyncTaskService(AsyncTaskService asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    public abstract TaskResult proceed() throws Exception;

    public void start() throws Exception {

        new Thread() {
            @Transactional
            public void run() {
                process();
            }
        }.start();
    }

    @Transactional
    private void process() {
        long createTime = task.getSubmitDate().getTime();
        try {
            TaskResult taskResult = proceed();
            task.setResult(taskResult.getResult());
            task.setRemark(taskResult.getRemark());
            task.setStatus(TaskStatus.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            task.setStatus(TaskStatus.FAIL.getValue());
        }
        task.setTimeCost(System.currentTimeMillis() - createTime);
        asyncTaskService.complete(task);
    }
}
