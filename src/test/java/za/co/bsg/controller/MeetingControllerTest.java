package za.co.bsg.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingManagementService;


import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class MeetingControllerTest {

    private MockMvc mvc;

    @Mock
    private MeetingManagementService meetingManagementService;

    @Test
    public void testCreateMeeting_ShouldCMeeting() throws Exception {

        // Setup Fixtures
        Meeting meeting1 = buildMeeting("A&D Meeting", 12, "Not Started");

        mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();

        // Setup Expectation
        given(this.meetingManagementService.CreateMeeting(meeting1)).willReturn(meeting1);

        // Exercise SUT
         mvc.perform(get("/api/meeting/create"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        // Verify

    }

    @Test
    public void testAvailableMeetings_ShouldReturnAllAvailableMeetings() throws Exception {

       // Setup Fixtures
        Meeting meeting1 = buildMeeting("A&D Meeting", 12 , "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();
        // Setup Expectation
        given(this.meetingManagementService.GetAllMeetings()).willReturn(asList(meeting1));

        // Exercise SUT
        this.mvc.perform(get("/api/availableMeetings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("A&D Meeting")))
                .andExpect(jsonPath("$[0].createdBy", is(12)))
                .andExpect(jsonPath("$[0].status", is("Not Started")));

        // Verify
        verify(meetingManagementService, times(1)).GetAllMeetings();
        verifyNoMoreInteractions(meetingManagementService);
    }

    @Test
    public void testUserMeetings_ShouldReturnAllUserMeetings() throws Exception {
        // Setup Fixtures
        Meeting meeting1 = buildMeeting("Technology Meeting", 12, "Not Started");

        this.mvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingManagementService)).build();

        // Setup Expectation
        int userId = 12;
        given(this.meetingManagementService.GetMeetingsByUser(userId)).willReturn(singletonList(meeting1));

        // Exercise SUT
        this.mvc.perform(get("/api/{userId}/myMeetings", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Technology Meeting")))
                .andExpect(jsonPath("$[0].createdBy", is(12)))
                .andExpect(jsonPath("$[0].status", is("Not Started")));

        // Verify
        verify(meetingManagementService, times(1)).GetMeetingsByUser(userId);
        verifyNoMoreInteractions(meetingManagementService);

    }

    public Meeting buildMeeting(String name, int user, String status){
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setCreatedBy(user);
        meeting.setStatus(status);

        return meeting;
    }
}