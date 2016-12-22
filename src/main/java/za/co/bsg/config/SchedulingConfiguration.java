package za.co.bsg.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingDataService;
import za.co.bsg.services.api.BigBlueButtonAPI;

import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {
    @Autowired
    MeetingDataService meetingDataService;
    @Autowired
    BigBlueButtonAPI bigBlueButtonAPI;

    @Scheduled(cron = "0/30 * * * * ?") // Run every 30 seconds
    public void scheduler() {
        System.out.print("Big Blue Button Health Scheduling at " + new Date() + ": ");
        CheckMeetingStatus();
    }

    private void CheckMeetingStatus() {
        UpdatedStartedMeetings();
        UpdatedEndedMeetings();

    }

    private void UpdatedStartedMeetings() {
        // TODO : Having a query to fetch all NOT started can simplify these two below request
        // TODO : Some meetings are being saved for no reason, might cause lock if other are using them
        List<Meeting> meetings = meetingDataService.retrieveAllByStatus(MeetingStatusEnum.NotStarted.toString());
        meetings.addAll(meetingDataService.retrieveAllByStatus(MeetingStatusEnum.Ended.toString()));
        int started = 0;
        for (Meeting meeting : meetings) {
            boolean meetingRunning = bigBlueButtonAPI.isMeetingRunning(meeting);
            if (meetingRunning) {
                meeting.setStatus(MeetingStatusEnum.Started.toString());
                started++;
            }
        }

        meetingDataService.save(meetings);
        System.out.print(started + " started, ");
    }

    private void UpdatedEndedMeetings() {
        //TODO : Some meetings are being saved for no reason, might cause lock if other are using them
        List<Meeting> meetings = meetingDataService.retrieveAllByStatus(MeetingStatusEnum.Started.toString());

        int ended = 0;
        for (Meeting meeting : meetings) {
            boolean meetingRunning = bigBlueButtonAPI.isMeetingRunning(meeting);
            if (!meetingRunning) {
                meeting.setStatus(MeetingStatusEnum.Ended.toString());
                ended++;
            }
        }

        meetingDataService.save(meetings);
        System.out.println(ended + " ended.");
    }
}