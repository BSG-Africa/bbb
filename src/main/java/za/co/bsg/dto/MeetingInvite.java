package za.co.bsg.dto;

import java.io.Serializable;

public class MeetingInvite implements Serializable {
    public String fullName;
    public String inviteURL;

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
}
