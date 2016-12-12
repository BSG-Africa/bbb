package za.co.bsg.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "meeting")
public class Meeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String meetingId;

    @Column
    private String name;

    @Column
    private String moderatorPassword;

    @Column
    private String attendeePassword;

    @Column
    private long createdBy;

    @ManyToOne
    private User moderator;

    @Column
    private Date createdDate;

    @Column
    private int modifiedBy;

    @Column
    private Date modifiedDate;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private String welcomeMessage;

    @Column
    private String agenda;

    @Column
    private String defaultPresentationURL;

    @Column
    private String logoutURL;

    @Column
    private int voiceBridge;

    @Column
    private String status;

    @Column
    private String moderatorURL;

    @Column
    private String inviteURL;

    @Transient
    private Map<String, String> meta = new HashMap<String, String>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModeratorURL() {
        return moderatorURL;
    }

    public void setModeratorURL(String moderatorURL) {
        this.moderatorURL = moderatorURL;
    }

    public String getInviteURL() {
        return inviteURL;
    }

    public void setInviteURL(String inviteURL) {
        this.inviteURL = inviteURL;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModeratorPassword() {
        return moderatorPassword;
    }

    public void setModeratorPassword(String moderatorPassword) {
        this.moderatorPassword = moderatorPassword;
    }

    public String getAttendeePassword() {
        return attendeePassword;
    }

    public void setAttendeePassword(String attendeePassword) {
        this.attendeePassword = attendeePassword;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getDefaultPresentationURL() {
        return defaultPresentationURL;
    }

    public void setDefaultPresentationURL(String defaultPresentationURL) {
        this.defaultPresentationURL = defaultPresentationURL;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    public int getVoiceBridge() {
        return voiceBridge;
    }

    public void setVoiceBridge(int voiceBridge) {
        this.voiceBridge = voiceBridge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }
    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }
}
