package za.co.bsg.services;


import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import za.co.bsg.model.Meeting;

import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
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
}