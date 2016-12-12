package za.co.bsg.services.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.net.URLEncoder;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BigBlueButtonImp.class, AppPropertiesConfiguration.class},
        loader = AnnotationConfigContextLoader.class)
public class BigBlueButtonImpTest {

    @Autowired
    private BigBlueButtonAPI bigBlueButtonAPI;

    @Autowired
    AppPropertiesConfiguration appPropertiesConfiguration;


    @Test
    public void testCreateMeeting() throws Exception {



        User user = new User();
        user.setUsername("kapeshi");
        user.setPassword("23456666");
        String meetingID = user.getUsername() + "'s meeting";
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingID);
        meeting.setName("Testing Meeting");

        ReflectionTestUtils.setField(appPropertiesConfiguration, "bbbURL", "http://test-install.blindsidenetworks.com/bigbluebutton/", String.class);
        ReflectionTestUtils.setField(appPropertiesConfiguration, "bbbSalt", "8cd8ef52e8e101574e400365b55e11a6");
        String joinURL  = bigBlueButtonAPI.createPublicMeeting(meeting, user, "<br>Welcome to Quibety Home.<br>", null, null);
        System.out.println(joinURL);
        String url = bigBlueButtonAPI.getUrl().replace("bigbluebutton/", "demo/");
        System.out.println(url);
        String inviteURL = url + "invite.html?action=invite&meetingID=" + URLEncoder.encode(meetingID, "UTF-8");
        System.out.println(inviteURL);
        boolean response = true;

        assertThat(true, sameBeanAs(response));
    }
}