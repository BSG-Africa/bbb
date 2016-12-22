package za.co.bsg.dto;

import java.io.Serializable;

public class MeetingInvite implements Serializable {
    public String fullName;
    public String meetingStatus;
    public String inviteURL;
    public String meetingName;


    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInviteURL() {
        return inviteURL;
    }

    public void setInviteURL(String inviteURL) {
        this.inviteURL = inviteURL;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }
}
