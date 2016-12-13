package za.co.bsg.services.api.response;

public class CreateMeeting extends BigBlueButtonResponse {
    private String moderatorPW;
    private String meetingID;
    private String dialNumber;
    private String voiceBridge;
    private String duration;
    private String hasBeenForciblyEnded;
    private String createTime;
    private String hasUserJoined;
    private String attendeePW;
    private String parentMeetingID;
    private String internalMeetingID;
    private String createDate;

    public String getModeratorPW() {
        return moderatorPW;
    }

    public void setModeratorPW(String moderatorPW) {
        this.moderatorPW = moderatorPW;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }

    public String getVoiceBridge() {
        return voiceBridge;
    }

    public void setVoiceBridge(String voiceBridge) {
        this.voiceBridge = voiceBridge;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHasBeenForciblyEnded() {
        return hasBeenForciblyEnded;
    }

    public void setHasBeenForciblyEnded(String hasBeenForciblyEnded) {
        this.hasBeenForciblyEnded = hasBeenForciblyEnded;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHasUserJoined() {
        return hasUserJoined;
    }

    public void setHasUserJoined(String hasUserJoined) {
        this.hasUserJoined = hasUserJoined;
    }

    public String getAttendeePW() {
        return attendeePW;
    }

    public void setAttendeePW(String attendeePW) {
        this.attendeePW = attendeePW;
    }

    public String getParentMeetingID() {
        return parentMeetingID;
    }

    public void setParentMeetingID(String parentMeetingID) {
        this.parentMeetingID = parentMeetingID;
    }

    public String getInternalMeetingID() {
        return internalMeetingID;
    }

    public void setInternalMeetingID(String internalMeetingID) {
        this.internalMeetingID = internalMeetingID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
