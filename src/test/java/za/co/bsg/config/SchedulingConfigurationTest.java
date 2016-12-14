package za.co.bsg.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.MeetingDataService;
import za.co.bsg.services.api.BigBlueButtonAPI;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SchedulingConfigurationTest {

    @InjectMocks
    private SchedulingConfiguration schedulingConfigService;

    @Mock
    private MeetingDataService meetingDataService;

    @Mock
    private BigBlueButtonAPI bigBlueButtonAPI;

    @Test
    public void schedulerWhenMeetingIsNotRunning_ShouldUpdateDatabaseStatus() throws Exception {
        // Set up fixture
        Meeting endedMeeting = buildMeeting(121L, "Communications Toolkit", null);
        ArrayList<Meeting> meetings = new ArrayList<Meeting>();
        endedMeeting.setStatus(MeetingStatusEnum.Started.toString());
        meetings.add(endedMeeting);
        ArrayList<Meeting> expectedMeetings = new ArrayList<Meeting>();
        expectedMeetings.add(endedMeeting);

        // Expectations
        ArgumentCaptor<Meeting> argument = ArgumentCaptor.forClass(Meeting.class);
        when(meetingDataService.retrieveAllByStatus(MeetingStatusEnum.Started.toString())).thenReturn(meetings);
        when(bigBlueButtonAPI.isMeetingRunning(endedMeeting)).thenReturn(false);

        // Exercise SUT
        schedulingConfigService.scheduler();

        // Verify
        verify(meetingDataService).save(expectedMeetings);
        assertEquals(MeetingStatusEnum.Ended.toString(), endedMeeting.getStatus());
    }

    public Meeting buildMeeting(Long id, String name, User createdBy) {
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setName(name);
        meeting.setCreatedBy(createdBy);
        return meeting;
    }
}