package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.api.BigBlueButtonAPI;
import za.co.bsg.services.api.exception.BigBlueButtonException;
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

    /**
     * Returns a Meeting object that was created.
     * This method always checks if meeting has been created before
     * by checking if a meetingId has been assigned to the meeting,
     * If the meeting hasnt been created before, create a bbb meeting
     * and then persist to the database. Alternatively it will just
     * create a bbb meeting again and return the same meeting - This
     * ensures that all meetings exist on bbb server.
     *
     * @param meeting a meeting object created as the front end form is submitted
     */
    public Meeting createMeeting(Meeting meeting) throws UnsupportedEncodingException {
        User user = userDataService.findUserById(meeting.getModerator().getId());
        if (meeting.getMeetingId() == null) {
            // Meeting has never been created
            meeting.setMeetingId(utilService.generateMeetingId());
            String moderatorURL = bigBlueButtonAPI.createPublicMeeting(meeting, user);
            String url = bigBlueButtonAPI.getUrl().replace("bigbluebutton/", "bbb-ui/");
            String inviteURL = url + "#/invite?meetingID=" + URLEncoder.encode(meeting.getMeetingId(), "UTF-8");
            meeting.setModeratorURL(moderatorURL);
            meeting.setInviteURL(inviteURL);

            return meetingDataService.save(meeting);
        } else {
            // Meeting has been created but expired in bbb
            bigBlueButtonAPI.createPublicMeeting(meeting, user);
            return meeting;
        }
    }

    /**
     * Returns a Meeting object that was saved(Updated).
     * This method takes a meeiting object and updates the database
     * with the updated
     *
     * @param meeting a meeting object to be updated in the database
     */
    public Meeting editMeeting(Meeting meeting) {
        return meetingDataService.save(meeting);
    }

    public String getInviteURL(String name, String meetingId)  {
        return bigBlueButtonAPI.getPublicJoinURL(name, meetingId);
    }

    /**
     * Returns a list of Meeting objects.
     * This method retrieve all meetings except the one with ended status in the meeting table and
     * removes all meetings that are either created by or being
     * moderated by the supplied in user
     *
     * @param userId a long data type - Expected to be current logged in user
     */
    public List<Meeting> getAllMeetings(long userId) {
        // TODO : This can be done in a single query.
        List<Meeting> allMeetings = meetingDataService.retrieveAllExcludeStatus(MeetingStatusEnum.Ended.toString());
        List<Meeting> myMeetings = this.getMeetingsByUser(userId);
        allMeetings.removeAll(myMeetings);
        return allMeetings;
    }

    /**
     * Returns a list of Meeting objects.
     * This method retrieve all meetings in the meeting table that
     * are either created by or being moderated by the supplied in user
     *
     * @param userId a long data type - Expected to be current logged in user
     */
    public List<Meeting> getMeetingsByUser(long userId) {
        User user = userDataService.findUserById(userId);
        List<Meeting> creatorMeetings = meetingDataService.retrieveAllByUserId(user);
        List<Meeting> moderatorMeetings = meetingDataService.retrieveAllByModerator(user);
        return meetingDataService.union(creatorMeetings, moderatorMeetings);
    }

    /**
     * Returns ResponseEntity of Meeting object.
     * This method attempts to delete a meeting from the database if it exists,
     * And if the meeting does not exist in the database, it returns an
     * HttpStatus of no content
     *
     * @param meetingId a long data type - Which is the Id of the meeting
     *                  to be deleted
     */
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

    /**
     * Returns a Meeting object.
     * This method retrieves a meeting from the database,
     *
     * @param meetingId a long data type - Which is the Id of the meeting
     *                  to be retrieved
     */
    public Meeting getMeeting(Long meetingId) {
        return meetingDataService.retrieve(meetingId);
    }

    /**
     * Returns a Meeting object.
     * This method retrieves a meeting from the database,
     *
     * @param meetingId a String object- Which is the bbb meetingId of the meeting
     *                  to be retrieved
     */
    public Meeting getMeetingByMeetingId(String meetingId) {
        return meetingDataService.retrieveByMeetingId(meetingId);
    }

    /**
     * Returns a boolean.
     * This method retrieves the meeting status through the bbb api ,
     *
     * @param meetingId a String object- Which is the bbb meetingId of the meeting
     *                  to be retrieved
     */
    public boolean isBBBMeetingRunning(String meetingId) throws BigBlueButtonException {
        return bigBlueButtonAPI.isMeetingRunning(meetingId);
    }
}
