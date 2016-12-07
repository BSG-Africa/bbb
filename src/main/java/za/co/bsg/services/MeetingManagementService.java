package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.dataAccess.MeetingDAO;
import za.co.bsg.model.Meeting;

@Service
public class MeetingManagementService {
    MeetingDAO meetingDAO;

    @Autowired
    public MeetingManagementService(MeetingDAO meetingDAO){
        this.meetingDAO = meetingDAO;
    }

    public Meeting CreateMeeting(Meeting meeting) {
        // Communicate to DB - persist
        Meeting persistedMeeting = meetingDAO.Save(meeting);
        // Communicate to BBB
        return persistedMeeting;
    }
}
