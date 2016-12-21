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

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class MeetingControllerTest {

    private MockMvc mvc;

    @Mock
    private MeetingManagementService meetingManagementService;

    @Test
    public void testAvailableMeetings_ShouldReturnAllAvailableMeetings() throws Exception {

        // Setup Fixtures
        long userId = 12;
        User user = new User();
        user.setName("Harry Peterson");
        user.setId(12);
        Meeting meeting1 = buildMeeting("A&D Meeting", user, null, "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();
        // Setup Expectation
        given(this.meetingManagementService.getAllMeetings(userId)).willReturn(singletonList(meeting1));

        // Exercise SUT and Verify
        this.mvc.perform(get("/api/meeting/available/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("A&D Meeting")))
                .andExpect(jsonPath("$[0].createdBy.name", is(user.getName())))
                .andExpect(jsonPath("$[0].status", is("Not Started")));
    }

    @Test
    public void testUserMeetings_ShouldReturnAllUserMeetings() throws Exception {

        // Setup Fixtures
        User moderator = new User();
        moderator.setName("Julia Smith");

        Meeting meeting1 = buildMeeting("Technology Meeting", null, moderator , "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();

        // Setup Expectation
        long userId = 12l;
        given(this.meetingManagementService.getMeetingsByUser(userId)).willReturn(singletonList(meeting1));

        // Exercise SUT and Verify
        this.mvc.perform(get("/api/meeting/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Technology Meeting")))
                .andExpect(jsonPath("$[0].moderator.name", is(moderator.getName())))
                .andExpect(jsonPath("$[0].status", is("Not Started")));
    }

    public Meeting buildMeeting(String name, User user, User moderator, String status) {
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setCreatedBy(user);
        meeting.setModerator(moderator);
        meeting.setStatus(status);

        return meeting;
    }
}