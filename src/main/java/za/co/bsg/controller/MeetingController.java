package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingManagementService;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MeetingController {

    private MeetingManagementService meetingManagementService;

    @Autowired
    public MeetingController(MeetingManagementService meetingManagementService){
        this.meetingManagementService = meetingManagementService;
    }

    @RequestMapping(value = "/meeting/{userId}", method = RequestMethod.GET)
    public List<Meeting> userMeetings(@PathVariable("userId") long userId){
        return meetingManagementService.getMeetingsByUser(userId);
    }

    @RequestMapping(value = "/meeting/available/{userId}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Meeting> availableMeetings(@PathVariable("userId") long userId) {
        return meetingManagementService.getAllMeetings(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/meeting/create", method = RequestMethod.POST)
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = new Meeting();
        try {
            persistedMeeting = meetingManagementService.createMeeting(meeting);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/meeting/edit", method = RequestMethod.POST)
    public ResponseEntity<Meeting> editMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = meetingManagementService.editMeeting(meeting);
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/meeting/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Meeting> deleteMeeting(@PathVariable Long id) {
        return meetingManagementService.deleteMeeting(id);
    }

    @RequestMapping(value = "/meeting/retrieve/{id}", method = RequestMethod.GET)
    public Meeting getMeeting(@PathVariable Long id) {
        return meetingManagementService.getMeeting(id);
    }
}
