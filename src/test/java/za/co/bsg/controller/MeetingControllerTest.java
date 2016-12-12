package za.co.bsg.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.MeetingManagementService;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class MeetingControllerTest {

    private MockMvc mvc;

    @Mock
    private MeetingManagementService meetingManagementService;

    @Test
    public void testCreateMeeting_ShouldCMeeting() throws Exception {

        User user = new User();
        user.setId(12);
        // Setup Fixtures
        Meeting meeting1 = buildMeeting("A&D Meeting", user, "Not Started");

        mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();

        // Setup Expectation
        given(this.meetingManagementService.createMeeting(meeting1)).willReturn(meeting1);

        // Exercise SUT
         mvc.perform(get("/api/meeting/create"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        // Verify

    }

    @Test
    public void testAvailableMeetings_ShouldReturnAllAvailableMeetings() throws Exception {

        // Setup Fixtures
        User user = new User();
        user.setId(12);
        Meeting meeting1 = buildMeeting("A&D Meeting", user, "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();
        // Setup Expectation
        given(this.meetingManagementService.getAllMeetings()).willReturn(asList(meeting1));

        // Exercise SUT
        this.mvc.perform(get("/api/availableMeetings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("A&D Meeting")))
                .andExpect(jsonPath("$[0].createdBy.id", is((int) user.getId())))
                .andExpect(jsonPath("$[0].status", is("Not Started")));

        // Verify
        verify(meetingManagementService, times(1)).getAllMeetings();
        verifyNoMoreInteractions(meetingManagementService);
    }

    @Test
    public void testUserMeetings_ShouldReturnAllUserMeetings() throws Exception {
        // Setup Fixtures
        User user = new User();
        user.setId(12);
        Meeting meeting1 = buildMeeting("Technology Meeting", user, "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();

        // Setup Expectation
        int userId = 12;
        given(this.meetingManagementService.getMeetingsByUser(user.getId())).willReturn(singletonList(meeting1));

        // Exercise SUT
        this.mvc.perform(get("/api/{userId}/myMeetings", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Technology Meeting")))
                .andExpect(jsonPath("$[0].createdBy", is(12)))
                .andExpect(jsonPath("$[0].status", is("Not Started")));

        // Verify
        verify(meetingManagementService, times(1)).getMeetingsByUser(user.getId());
        verifyNoMoreInteractions(meetingManagementService);

    }

    public Meeting buildMeeting(String name, User user, String status) {
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setCreatedBy(user);
        meeting.setStatus(status);

        return meeting;
    }
}