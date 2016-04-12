package com.qqj.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource({"classpath*:application-*.xml"})
public class ScheduleInitializer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ScheduleInitializer.class);
    }
}