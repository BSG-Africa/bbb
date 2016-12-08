package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public List<Meeting> findAllUserMeetings(int userId){
        return meetingDataService.findByCreatedBy(userId);
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

    public List<Meeting> GetMeetingsByUser(int userId) {
        List<Meeting> meetings = new ArrayList<Meeting>();
        Meeting m = new Meeting();
        m.setName("Induction");
        m.setStatus("Not Started");
        m.setCreatedBy(userId);
        meetings.add(m);
        return meetingDataService.RetrieveAll();
    }

    public ResponseEntity<Meeting> deleteMeeting(Long meetingId) {
        Meeting meetingToDelete = meetingDataService.retrieve(meetingId);
        meetingDataService.delete(meetingId);
        Meeting deletedMeeting = meetingDataService.retrieve(meetingId);

        if (deletedMeeting == null || (deletedMeeting != null && meetingToDelete.getId().equals(deletedMeeting.getId()))) {
            return new ResponseEntity<Meeting>(meetingToDelete, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Meeting>(meetingToDelete, HttpStatus.OK);
        }
    }
}
