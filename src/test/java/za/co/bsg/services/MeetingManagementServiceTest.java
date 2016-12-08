package za.co.bsg.services;


import com.shazam.shazamcrest.matcher.Matchers;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.bsg.model.Meeting;

import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeetingManagementServiceTest {

    private MeetingManagementService meetingManagementService;
    @Mock
    private MeetingDataService meetingDataService;

    @Before
    public void init() {
        meetingManagementService = new MeetingManagementService(meetingDataService);
    }
    
    @Test
    public void findAllUserMeetings_ShouldReturnMeetingsOfCurrentLoggedOnModerator(){

        // Setup fixture
        Meeting moderatorMeeting = buildMeeting(1L, "Technology Meeting", 12);

        // TODO: Tiyani : Integration Test
        //Meeting nonCurrentModeratorMeeting = buildMeeting(2L, "A&D Meeting", "ModeratorName");
        int moderator = 12;

        // Set Expectations
        when(meetingDataService.findByCreatedBy(moderator)).thenReturn(singletonList(moderatorMeeting));
        List<Meeting> expectedMeeting = singletonList(moderatorMeeting);

        // Exercise SUT
        List<Meeting> actualMeeting = meetingManagementService.findAllUserMeetings(moderator);

        // Verify
        assertThat(actualMeeting, Matchers.sameBeanAs(expectedMeeting));
    }

    @Test
    public void CreateMeetingShouldPersistMeetingAndReturnResult() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");

        // Expectations
        when(meetingDataService.Save(meeting)).thenReturn(meeting);

        // Exercise SUT
        Meeting actualMeeting  = meetingManagementService.CreateMeeting(meeting);

        assertThat(actualMeeting, CoreMatchers.is(sameBeanAs(meeting)));

    }

    @Test
    public void GetAllMeetingsShouldReturnAllMeetingsInDatabase() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");

        // Expectations
        when(meetingDataService.RetrieveAll()).thenReturn(Collections.singletonList(meeting));

        // Exercise SUT
        List<Meeting> actualMeetings  = meetingManagementService.GetAllMeetings();

        assertThat(actualMeetings, CoreMatchers.is(sameBeanAs(Collections.singletonList(meeting))));
    }

    @Test
    public void GetMeetingsByUserShouldReturnAllMeetingsByUserId() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");

        // Expectations
        when(meetingDataService.RetrieveAll()).thenReturn(Collections.singletonList(meeting));

        // Exercise SUT
        List<Meeting> actualMeetings  = meetingManagementService.GetAllMeetings();

        assertThat(actualMeetings, CoreMatchers.is(sameBeanAs(Collections.singletonList(meeting))));
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

        assertThat(actualMeetings, CoreMatchers.is(sameBeanAs(expectedMeeting)));
    }


    public Meeting buildMeeting(Long id, String name, int createdBy){
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setName(name);
        meeting.setCreatedBy(createdBy);
        return meeting;
    }
}