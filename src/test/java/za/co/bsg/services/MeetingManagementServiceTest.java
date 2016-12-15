package za.co.bsg.services;


import com.shazam.shazamcrest.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.api.BigBlueButtonAPI;
import za.co.bsg.util.UtilService;

import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
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
    public void findAllUserMeetings_ShouldReturnMeetingsOfCurrentLoggedOnModerator(){

        User user = new User();
        user.setId(12);
        // Setup fixture
        Meeting moderatorMeeting = buildMeeting(1L, "Technology Meeting", user);

        // TODO: Tiyani : Integration Test
        //Meeting nonCurrentModeratorMeeting = buildMeeting(2L, "A&D Meeting", "ModeratorName");
        int moderator = 12;

        // Set Expectations
        when(meetingDataService.retrieveAllByUserId(user)).thenReturn(singletonList(moderatorMeeting));
        List<Meeting> expectedMeeting = singletonList(moderatorMeeting);

        // Exercise SUT
        List<Meeting> actualMeeting = meetingManagementService.getMeetingsByUser(user.getId());

        // Verify
        assertThat(actualMeeting, Matchers.sameBeanAs(expectedMeeting));
    }

    @Test
    public void CreateMeetingWhenMeetingHasNeverBeenAssignedBBBDetails_ShouldPersistMeetingAndReturnResult() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        User user = new User();
        user.setId(11);
        meeting.setCreatedBy(user);
        meeting.setName("C1/D1 Induction");
        meeting.setWelcomeMessage("");

        user.setUsername("KapeshiKongolo");
        user.setPassword("123444");

        // Expectations
        when(meetingDataService.save(meeting)).thenReturn(meeting);
        when(utilService.generateMeetingId()).thenReturn("123434");
        when(userDataService.findUserById(meeting.getCreatedBy().getId())).thenReturn(user);
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
        meeting.setCreatedBy(user);
        meeting.setName("C1/D1 Induction");
        meeting.setWelcomeMessage("");
        meeting.setMeetingId("meetingId12358");
        user.setUsername("KapeshiKongolo");
        user.setPassword("123444");

        // Expectations
        when(meetingDataService.save(meeting)).thenReturn(meeting);
        when(utilService.generateMeetingId()).thenReturn("123434");
        when(userDataService.findUserById(meeting.getCreatedBy().getId())).thenReturn(user);
        when(bigBlueButtonAPI.createPublicMeeting(meeting, user)).thenReturn("http://localhost/bigbluebutton/");
        when(bigBlueButtonAPI.getUrl()).thenReturn("http://localhost/bigbluebutton/");

        // Exercise SUT
        Meeting actualMeeting = meetingManagementService.createMeeting(meeting);

        assertThat(actualMeeting, is(sameBeanAs(meeting)));

    }

    @Test
    public void GetAllMeetingsShouldReturnAllMeetingsInDatabase() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");
        long userId = 12;

        // Expectations
        when(meetingDataService.retrieveAll()).thenReturn(Collections.singletonList(meeting));

        // Exercise SUT
        List<Meeting> actualMeetings = meetingManagementService.getAllMeetings(userId);

        assertThat(actualMeetings, is(sameBeanAs(Collections.singletonList(meeting))));
    }

    @Test
    public void GetMeetingsByUserShouldReturnAllMeetingsByUserId() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");
        long userId = 12;

        // Expectations
        when(meetingDataService.retrieveAll()).thenReturn(Collections.singletonList(meeting));

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
        ResponseEntity<Meeting> actualMeetings = meetingManagementService.deleteMeeting((long) meetingId);

        assertThat(actualMeetings, is(sameBeanAs(expectedMeeting)));
    }


    public Meeting buildMeeting(Long id, String name, User createdBy) {
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setName(name);
        meeting.setCreatedBy(createdBy);
        return meeting;
    }
}