package za.co.bsg.services;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.BigBlueButtonApplication;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.repository.MeetingRepository;
import za.co.bsg.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.is;


@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BigBlueButtonApplication.class, MeetingRepository.class, UserRepository.class},
        loader = AnnotationConfigContextLoader.class)
@TestPropertySource(locations="classpath:test.properties")
@Transactional
@DirtiesContext
public class MeetingDataServiceTest {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        //Setup user in db
        User user = new User();
        user.setName("Kapeshi");
        user.setUsername("kapeshi.kongolo");
        user.setPassword("123456");
        userRepository.save(user);

        //Setup meeting in db
        Meeting meeting = new Meeting();
        meeting.setMeetingId("Test1");
        meeting.setName("Testing 1");
        meeting.setCreatedBy(user);
        meeting.setModerator(user);
        meeting.setStatus("Started");
        meetingRepository.save(meeting);
    }

    @Test
    public void findByStatus_ShouldReturnMeetingListFoundByStatus() {
        // Setup fixture
        String meetingStatus = "Started";

        // Expectations
        User user = new User();
        user.setId(1l);
        user.setName("Kapeshi");
        user.setUsername("kapeshi.kongolo");
        user.setPassword("123456");

        Meeting meeting = new Meeting();
        meeting.setId(1l);
        meeting.setMeetingId("Test1");
        meeting.setName("Testing 1");
        meeting.setCreatedBy(user);
        meeting.setModerator(user);
        meeting.setStatus("Started");

        // Exercise SUT
        List<Meeting> actualMeetings = meetingRepository.findByStatus(meetingStatus);

        //Verify
        assertThat(actualMeetings, is(sameBeanAs(Collections.singletonList(meeting))));
    }
}