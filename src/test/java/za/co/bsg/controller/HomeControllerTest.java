package za.co.bsg.controller;

import junit.framework.TestCase;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class HomeControllerTest extends TestCase {

    @Mock
    private MeetingManagementService meetingManagementService;
    private MockMvc mvc;

    @Test
    public void getMeetingStatus() throws Exception {
        // Setup Fixtures
        String meetingId = "efgv34efgv";
        this.mvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();

        // Setup Expectation
        when(this.meetingManagementService.isBBBMeetingRunning(meetingId)).thenReturn(true);

        // Exercise SUT
        this.mvc.perform(get("/invite/retrieveMeetingStatus/"+meetingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$", is("true")));

        // Verify
        verify(meetingManagementService, times(1)).isBBBMeetingRunning(meetingId);
        verifyNoMoreInteractions(meetingManagementService);
    }



    public void testGetMeetingStatus() throws Exception {

    }
}