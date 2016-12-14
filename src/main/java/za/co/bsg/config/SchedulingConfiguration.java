package za.co.bsg.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {

    @Scheduled(cron = "* 0/5 * * * ?") // Run every 30 seconds
    public void scheduler() {
        System.out.println("Big Blue Button Health Scheduling  " + new Date());
    }
}