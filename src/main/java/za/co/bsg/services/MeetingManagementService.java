package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;

@Service
public class MeetingManagementService {
    MeetingDataService meetingDataService;

    @Autowired
    public MeetingManagementService(MeetingDataService meetingDataService){
        this.meetingDataService = meetingDataService;
    }

    public Meeting CreateMeeting(Meeting meeting) {
        // Communicate to DB - persist
        Meeting persistedMeeting = meetingDataService.Save(meeting);
        // Communicate to BBB
        return persistedMeeting;
    }
}
