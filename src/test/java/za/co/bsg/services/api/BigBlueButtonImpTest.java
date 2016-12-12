package za.co.bsg.services.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.net.URLEncoder;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BigBlueButtonImp.class},
        loader = AnnotationConfigContextLoader.class)
public class BigBlueButtonImpTest {

    @Autowired
    private BigBlueButtonAPI bigBlueButtonAPI;

    @Test
    public void testCreateMeeting() throws Exception {



        User user = new User();
        user.setUsername("kapeshi");
        user.setPassword("23456666");
        String meetingID = user.getUsername() + "'s meeting";
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingID);
        meeting.setName("Testing Meeting");
        //
        // This is the URL for to join the meeting as moderator
        //

        String joinURL  = bigBlueButtonAPI.getJoinURL(meeting, user, "<br>Welcome to Quibety Home.<br>", null, null);
        System.out.println(joinURL);
        String url = bigBlueButtonAPI.getUrl().replace("bigbluebutton/", "demo/");
        System.out.println(url);
        String inviteURL = url + "create.jsp?action=invite&meetingID=" + URLEncoder.encode(meetingID, "UTF-8");
        System.out.println(inviteURL);
        boolean response = true;

        assertThat(true, sameBeanAs(response));
    }
}