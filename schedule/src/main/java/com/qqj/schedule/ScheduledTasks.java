package com.qqj.schedule;

import com.qqj.schedule.facade.ScheduleFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {

    @Autowired
    private ScheduleFacade scheduleFacade;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteExcels() {
        scheduleFacade.deleteExcels();
    }

    @Scheduled(cron = "0/1 * * * * ?")
    public void test() {
        System.out.println("test");
    }
}