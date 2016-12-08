package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;

import java.util.List;

@Service
public class MeetingManagementService {
    MeetingDataService meetingDataService;

    @Autowired
    public MeetingManagementService(MeetingDataService meetingDataService){
        this.meetingDataService = meetingDataService;
    }

    public  List<Meeting> findAllMeetings(){
        return meetingDataService.findAll();
    }

    public List<Meeting> findAllUserMeetings(String moderator){
        return meetingDataService.findByCreatedBy(moderator);
    }

    public Meeting CreateMeeting(Meeting meeting) {
        // TODO: Ivhani: Check Code Analysis and implement suggestions where applicable
        // Communicate to DB - persist
        Meeting persistedMeeting = meetingDataService.Save(meeting);
        // Communicate to BBB
        return persistedMeeting;
    }

    public List<Meeting> GetAllMeetings() {
        return meetingDataService.RetrieveAll();
    }
}
