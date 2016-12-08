package za.co.bsg.services.api;

import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.util.Map;

public interface BigBlueButtonAPI {

    public String getUrl();

    public String getSalt();

    public Meeting createMeeting(Meeting meeting) throws BigBlueButtonException;

    public boolean getMeetingStatus(String meetingID) throws BigBlueButtonException;

    public Map<String, Object> getMeetingInfo(String meetingID, String password) throws BigBlueButtonException;

    public boolean endMeeting(String meetingID, String password) throws BigBlueButtonException;

    public String getJoinMeetingURL(String username, String meetingID, String password, String clientURL);

    public String getJoinURL(Meeting meeting, User user, String xmlParam);

    public String getMeetings() throws BigBlueButtonException;
}
