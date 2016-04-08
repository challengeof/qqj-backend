package com.mishu.cgwy.schedule;

import com.mishu.cgwy.schedule.facade.ScheduleFacade;
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

    @Scheduled(cron = "0 10 1 * * ?")
    public void rebuildIndex() throws Exception {
        scheduleFacade.rebuildIndex();
    }

    @Scheduled(cron = "0 0 19 * * ?")
    public void dailyPush() throws Exception {
        scheduleFacade.dailyPush();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void saveStockTotalDaily() throws Exception {
        scheduleFacade.saveStockTotalDaily();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void sendNotReceiveMail() throws Exception {
        scheduleFacade.sendNotReceiveOrderMail();
    }
}