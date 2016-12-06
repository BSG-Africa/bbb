package za.co.bsg.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.co.bsg.model.Meeting;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/meeting")
public class MeetingController {

    @RequestMapping(value = "/availableMeetings", method = RequestMethod.GET)
    public List<Meeting> availableMeetings(){
        // To be fetched from the repository
        List<Meeting> availableMeeting = new ArrayList<Meeting>() ;
        availableMeeting.add(new Meeting());
        return  availableMeeting;
    }

    @RequestMapping(value = "/userMeetings", method = RequestMethod.GET)
    public List<Meeting> userMeetings(){
        // To be fetched from the repository
        List<Meeting> userMeetings = new ArrayList<Meeting>() ;
        userMeetings.add(new Meeting());
        return  userMeetings;
    }

    @RequestMapping(value = "/createMeeting", method = RequestMethod.POST)
    public List<Meeting> createMeeting(){
        return new ArrayList<Meeting>();
    }
}
