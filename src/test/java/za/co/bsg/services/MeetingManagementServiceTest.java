package za.co.bsg.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.api.BigBlueButtonAPI;
import za.co.bsg.util.UtilService;

import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeetingManagementServiceTest {

    private MeetingManagementService meetingManagementService;
    @Mock
    private MeetingDataService meetingDataService;
    @Mock
    private UserDataService userDataService;
    @Mock
    private BigBlueButtonAPI bigBlueButtonAPI;
    @Mock
    private UtilService utilService;

    @Before
    public void init() {
        meetingManagementService = new MeetingManagementService(meetingDataService, userDataService, bigBlueButtonAPI, utilService);
    }

    @Test
    public void CreateMeetingWhenMeetingHasNeverBeenAssignedBBBDetails_ShouldPersistMeetingAndReturnResult() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        User user = new User();
        user.setId(11);
        meeting.setModerator(user);
        meeting.setName("C1/D1 Induction");
        meeting.setWelcomeMessage("");

        user.setUsername("KapeshiKongolo");
        user.setPassword("123444");

        // Expectations
        when(meetingDataService.save(meeting)).thenReturn(meeting);
        when(utilService.generateMeetingId()).thenReturn("123434");
        when(userDataService.findUserById(meeting.getModerator().getId())).thenReturn(user);
        when(bigBlueButtonAPI.createPublicMeeting(meeting, user)).thenReturn("http://localhost/bigbluebutton/");
        when(bigBlueButtonAPI.getUrl()).thenReturn("http://localhost/bigbluebutton/");

        // Exercise SUT
        Meeting actualMeeting = meetingManagementService.createMeeting(meeting);

        assertThat(actualMeeting, is(sameBeanAs(meeting)));

    }

    @Test
    public void CreateMeetingWhenHasAlreadyBeenAssignedBBBDetails_ShouldReturnMeeting() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        User user = new User();
        user.setId(11);
        meeting.setModerator(user);
        meeting.setName("C1/D1 Induction");
        meeting.setWelcomeMessage("");
        meeting.setMeetingId("meetingId12358");
        user.setUsername("KapeshiKongolo");
        user.setPassword("123444");

        // Expectations
        when(meetingDataService.save(meeting)).thenReturn(meeting);
        when(utilService.generateMeetingId()).thenReturn("123434");
        when(userDataService.findUserById(meeting.getModerator().getId())).thenReturn(user);
        when(bigBlueButtonAPI.createPublicMeeting(meeting, user)).thenReturn("http://localhost/bigbluebutton/");
        when(bigBlueButtonAPI.getUrl()).thenReturn("http://localhost/bigbluebutton/");

        // Exercise SUT
        Meeting actualMeeting = meetingManagementService.createMeeting(meeting);

        assertThat(actualMeeting, is(sameBeanAs(meeting)));

    }

    @Test
    public void GetAllMeetingsShouldReturnAllNotEndedMeetingsInDatabase() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");
        meeting.setStatus(MeetingStatusEnum.Ended.toString());
        long userId = 12;

        // Expectations
        when(meetingDataService.retrieveAllExcludeStatus(MeetingStatusEnum.Ended.toString())).thenReturn(Collections.singletonList(meeting));

        // Exercise SUT
        List<Meeting> actualMeetings = meetingManagementService.getAllMeetings(userId);

        assertThat(actualMeetings, is(sameBeanAs(Collections.singletonList(meeting))));
    }

    @Test
    public void DeleteMeetingWhenMeetingIsNotDeletedShouldReturnNoContent() throws Exception {
        // Setup fixture
        long meetingId = 101;
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");

        // Expectations
        when(meetingDataService.retrieve(meetingId)).thenReturn(meeting);
        ResponseEntity<Meeting> expectedMeeting = new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
        // Exercise SUT
        ResponseEntity<Meeting> actualMeetings = meetingManagementService.deleteMeeting(meetingId);

        assertThat(actualMeetings, is(sameBeanAs(expectedMeeting)));
    }



    @Test
    public void getMeetingStatusShouldReturnMeetingStatusFromBBBApi() throws Exception {
    public void DeleteMeetingWhenMeetingExists_ShouldReturnNoContent() throws Exception {
        // Setup fixture
        String meetingId = "dfe32fgdf";

        // Expectations
        when(bigBlueButtonAPI.isMeetingRunning(meetingId)).thenReturn(true);

        when(meetingDataService.retrieve(meetingId)).thenReturn(meeting);
        ResponseEntity<Meeting> expectedMeeting = new ResponseEntity<Meeting>(meeting, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<Meeting> actualMeetings = meetingManagementService.deleteMeeting(meetingId);

        assertThat(actualMeetings, is(sameBeanAs(expectedMeeting)));
    }

    @Test
    public void DeleteMeetingWhenMeetingDoesNotShouldReturnNoContent() throws Exception {
        // Setup fixture
        long meetingId = 101;

        // Expectations
        when(meetingDataService.retrieve(meetingId)).thenReturn(null);
        ResponseEntity<Meeting> expectedMeeting = new ResponseEntity<Meeting>(HttpStatus.NO_CONTENT);
        // Exercise SUT
        boolean actualMeetingStatus = meetingManagementService.isBBBMeetingRunning(meetingId);

        verify(bigBlueButtonAPI, times(1)).isMeetingRunning(meetingId);
        assertThat(actualMeetingStatus, is(sameBeanAs(true)));

    }

    public Meeting buildMeeting(Long id, String name, User createdBy) {
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setName(name);
        meeting.setCreatedBy(createdBy);
        return meeting;
    }
}