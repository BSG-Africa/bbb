package za.co.bsg.services.api;

import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

@Service
public interface BigBlueButtonAPI {

    String getUrl();

    String getSalt();

    String getPublicAttendeePW();

    String getPublicModeratorPW();

    String createPublicMeeting(Meeting meeting, User user);

    String getPublicJoinURL(String username, String meetingID);

    boolean isMeetingRunning(Meeting meeting);

    boolean endMeeting(String meetingID, String moderatorPassword);
}
