package za.co.bsg.repository;


import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import za.co.bsg.model.Meeting;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MeetingRepositoryTest {

    @Mock
    private MeetingRepository meetingRepository;

    public void testFindAllMeetings() throws Exception {
        // Setup Fixtures

        // Set Expectations
        Mockito.when(meetingRepository.findAllMeetings()).thenReturn(new ArrayList<Meeting>());

        // Exercise SUT
        List<Meeting> actualMeetings = meetingRepository.findAllMeetings();

        // Verify
    //    Assert.


    }

    public void testFindAllMeetingsByModeratorId() throws Exception {

    }

    public void testCreateMeeting() throws Exception {

    }
}