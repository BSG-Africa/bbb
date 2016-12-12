package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.api.BigBlueButtonAPI;
import za.co.bsg.util.UtilService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Service
public class MeetingManagementService {
    MeetingDataService meetingDataService;

    UserDataService userDataService;

    BigBlueButtonAPI bigBlueButtonAPI;

    UtilService utilService;

    @Autowired
    public MeetingManagementService(MeetingDataService meetingDataService, UserDataService userDataService, BigBlueButtonAPI bigBlueButtonAPI, UtilService utilService){
        this.meetingDataService = meetingDataService;
        this.userDataService = userDataService;
        this.bigBlueButtonAPI = bigBlueButtonAPI;
        this.utilService = utilService;
    }

    public Meeting createMeeting(Meeting meeting) throws UnsupportedEncodingException {
        User user = userDataService.findUserById(meeting.getCreatedBy().getId());
        meeting.setMeetingId(utilService.generateMeetingId());
        String moderatorURL  = bigBlueButtonAPI.createPublicMeeting(meeting, user, "<br>" + meeting.getWelcomeMessage() + "<br>", null, null);
        String url = bigBlueButtonAPI.getUrl().replace("bigbluebutton/", "bbb-ui/");
        String inviteURL = url + "#/invite?meetingID=" + URLEncoder.encode(meeting.getMeetingId(), "UTF-8");
        meeting.setModeratorURL(moderatorURL);
        meeting.setInviteURL(inviteURL);

        return meetingDataService.save(meeting);
    }

    public String getInviteURL(String name, String meetingId)  {
        return bigBlueButtonAPI.getPublicJoinURL(name, meetingId);
    }

    public List<Meeting> getAllMeetings(long userId) {
        List<Meeting> allMeetings = meetingDataService.retrieveAll();
        List<Meeting> myMeetings = this.getMeetingsByUser(userId);
        allMeetings.removeAll(myMeetings);
        return allMeetings;
    }

    public List<Meeting> getMeetingsByUser(long userId) {
        User user = userDataService.findUserById(userId);
        List<Meeting> creatorMeetings = meetingDataService.retrieveAllByUserId(user);
        List<Meeting> moderatorMeetings = meetingDataService.retrieveAllByModerator(user);
        List<Meeting> meetings = meetingDataService.union(creatorMeetings, moderatorMeetings);
        return meetings;
    }

    public ResponseEntity<Meeting> deleteMeeting(Long meetingId) {
        Meeting meetingToDelete = meetingDataService.retrieve(meetingId);
        meetingDataService.delete(meetingId);
        Meeting deletedMeeting = meetingDataService.retrieve(meetingId);

        if (deletedMeeting == null || (meetingToDelete.getId().equals(deletedMeeting.getId()))) {
            return new ResponseEntity<Meeting>(meetingToDelete, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Meeting>(meetingToDelete, HttpStatus.OK);
        }
    }

    public Meeting startMeeting(Meeting meeting) {
        boolean isValid = bigBlueButtonAPI.isMeetingRunning(meeting);
        return meeting;
    }
}
