package za.co.bsg.services.api;

import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.util.Map;

@Service
public interface BigBlueButtonAPI {

    public String getUrl();

    public String getSalt();

    public String getPublicAttendeePW();

    public String getPublicModeratorPW();

    public String createPublicMeeting(Meeting meeting, User user, String welcome, Map<String, String> metadata, String xml);

    public String getPublicJoinURL(String username, String meetingID);

    public String isMeetingRunning(Meeting meeting);
}
