package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingManagementService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MeetingController {

    private MeetingManagementService meetingManagementService;

    @Autowired
    public MeetingController(MeetingManagementService meetingManagementService){
        this.meetingManagementService = meetingManagementService;
    }

    @RequestMapping(value = "/availableMeetings", method = RequestMethod.GET)
    public @ResponseBody List<Meeting> availableMeetings(){
        return meetingManagementService.GetAllMeetings();
    }

    @RequestMapping(value = "/{userId}/myMeetings", method = RequestMethod.GET)
    public List<Meeting> userMeetings(@PathVariable("userId") int userId){
        return meetingManagementService.GetMeetingsByUser(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/meeting/create", method = RequestMethod.POST)
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = meetingManagementService.CreateMeeting(meeting);
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }
}
