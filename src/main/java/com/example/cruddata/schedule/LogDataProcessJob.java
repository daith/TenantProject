package com.example.cruddata.schedule;

import com.example.cruddata.repository.system.LogDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LogDataProcessJob {

    private static final Logger log = LoggerFactory.getLogger(LogDataProcessJob.class);

    @Autowired
    LogDataRepository logDataRepository;

    @Scheduled(cron = "0 0 12 * * ?", zone="Asia/Taipei")
    public void LogDataProcessJob(){
        /* log in db only 7 day data */
        log.info("LogDataProcessJob start");
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusDays(-7L);
        logDataRepository.deleteByEntryTimeLessThan(localDateTime);
    }
}
