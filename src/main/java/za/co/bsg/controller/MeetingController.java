package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingManagementService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/meeting")
public class MeetingController {

    private MeetingManagementService meetingManagementService;

    @Autowired
    public MeetingController(MeetingManagementService meetingManagementService){
        this.meetingManagementService = meetingManagementService;
    }

    @RequestMapping(value = "/availableMeetings", method = RequestMethod.GET)
    public List<Meeting> availableMeetings(){
        return meetingManagementService.findAllMeetings();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/userMeetings", method = RequestMethod.GET)
    public List<Meeting> userMeetings(@RequestBody Meeting meeting){
        String moderator = meeting.getCreatedBy();
        return meetingManagementService.findAllUserMeetings(moderator);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = meetingManagementService.CreateMeeting(meeting);
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }
}
