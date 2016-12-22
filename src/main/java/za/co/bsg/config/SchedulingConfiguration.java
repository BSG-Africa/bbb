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
        List<Meeting> meetings = meetingDataService.retrieveAllByStatus(MeetingStatusEnum.NotStarted.toString());
        meetings.addAll(meetingDataService.retrieveAllByStatus(MeetingStatusEnum.Ended.toString()));
        for (Meeting meeting : meetings) {
            boolean meetingRunning = bigBlueButtonAPI.isMeetingRunning(meeting);
            if (meetingRunning) {
                meeting.setStatus(MeetingStatusEnum.Started.toString());
            }
        }

        meetingDataService.save(meetings);
        System.out.print(meetings.size() + " started, ");
    }

    private void UpdatedEndedMeetings() {
        List<Meeting> meetings = meetingDataService.retrieveAllByStatus(MeetingStatusEnum.Started.toString());

        for (Meeting meeting : meetings) {
            boolean meetingRunning = bigBlueButtonAPI.isMeetingRunning(meeting);
            if (!meetingRunning) {
                meeting.setStatus(MeetingStatusEnum.Ended.toString());
            }
        }

        meetingDataService.save(meetings);
        System.out.println(meetings.size() + " ended.");
    }
}