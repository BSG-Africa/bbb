package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;

import java.util.ArrayList;
import java.util.List;

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

    public List<Meeting> GetAllMeetings() {
        return meetingDataService.RetrieveAll();
    }

    public List<Meeting> GetMeetingsByUser(int userId) {
        List<Meeting> meetings = new ArrayList<Meeting>();
        Meeting m = new Meeting();
        m.setName("Induction");
        m.setStatus("Not Started");
        m.setCreatedBy("Ivhani Mase");
        meetings.add(m);
        return meetings;
    }
}
