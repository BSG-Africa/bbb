package za.co.bsg.services;


import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import za.co.bsg.dataAccess.MeetingDAO;
import za.co.bsg.model.Meeting;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeetingManagementServiceTest {

    private MeetingManagementService meetingManagementService;
    @Mock
    private MeetingDAO meetingDAO;

    @Before
    public void init() {
        meetingManagementService = new MeetingManagementService(meetingDAO);
    }

    @Test
    public void CreateMeetingShouldPersistMeetingAndReturnResult() throws Exception {
        // Setup fixture
        Meeting meeting = new Meeting();
        meeting.setName("C1/D1 Induction");

        // Expectations
        when(meetingDAO.Save(meeting)).thenReturn(meeting);

        // Exercise SUT
        Meeting actualMeeting  = meetingManagementService.CreateMeeting(meeting);

        assertThat(actualMeeting, CoreMatchers.is(sameBeanAs(meeting)));

    }
}