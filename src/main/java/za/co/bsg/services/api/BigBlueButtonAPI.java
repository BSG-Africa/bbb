package za.co.bsg.services.api;

import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.util.Map;

@Service
public interface BigBlueButtonAPI {

    public String getUrl();

    public String getSalt();

    public Meeting createMeeting(Meeting meeting) throws BigBlueButtonException;

    public boolean getMeetingStatus(String meetingID) throws BigBlueButtonException;

    public Map<String, Object> getMeetingInfo(String meetingID, String password) throws BigBlueButtonException;

    public boolean endMeeting(String meetingID, String password) throws BigBlueButtonException;

    public String getJoinMeetingURL(String username, String meetingID, String password, String clientURL);

    public String getJoinURL(Meeting meeting, User user, String welcome, Map<String, String> metadata, String xml);

    public String getMeetings() throws BigBlueButtonException;
}
