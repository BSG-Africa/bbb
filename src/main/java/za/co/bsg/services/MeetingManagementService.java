package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public Meeting createMeeting(Meeting meeting) {
        return meetingDataService.save(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetingDataService.retrieveAll();
    }

    public List<Meeting> getMeetingsByUser(int userId) {
        return meetingDataService.retrieveAllByUserId(userId);
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
