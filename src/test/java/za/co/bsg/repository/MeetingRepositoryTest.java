package za.co.bsg.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MeetingRepositoryTest{

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    public void testFindByCreatedBy_ShouldReturnOnlyMeetingsForUser(){
        // Setup Fixtures
        User userToFindMeetingBy = buildUser("Helen Rose");
        User otherUser = buildUser("Deon Smith");

        this.entityManager.persist(userToFindMeetingBy);
        this.entityManager.persist(otherUser);

        Meeting userToFindByMeeting = buildMeeting("A&D Forum","Not Started", userToFindMeetingBy, null);
        Meeting otherUserMeeting = buildMeeting("Technology Meeting","Not Started", otherUser, null);

        this.entityManager.persist(userToFindByMeeting);
        this.entityManager.persist(otherUserMeeting);

        // Set Expectations
        List<Meeting> expectedMeetings = singletonList(userToFindByMeeting);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingRepository.findByCreatedBy(userToFindMeetingBy);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testFindByModerator_ShouldOnlyReturnMeetingsUserIsModeratorOf() {
        // Setup Fixtures
        User moderatorUser= buildUser("Helen Jones");
        User otherUser= buildUser("Helen Rose");
        User moderatorToFindBy = buildUser("Deon Smith");
        User otherModerator = buildUser("Kurt Swart");

        this.entityManager.persist(moderatorUser);
        this.entityManager.persist(otherUser);
        this.entityManager.persist(moderatorToFindBy);
        this.entityManager.persist(otherModerator);

        Meeting moderatorToFindByMeetingSelfCreated = buildMeeting("A&D Forum","Not Started", moderatorUser, moderatorToFindBy);
        Meeting moderatorToFindByMeetingNonSelfCreated = buildMeeting("Technology Meeting","Not Started", otherUser, moderatorToFindBy);
        Meeting otherModeratorMeetingNonSelfCreated = buildMeeting("Strategy Meeting","Not Started", otherUser, otherModerator);

        this.entityManager.persist(moderatorToFindByMeetingSelfCreated);
        this.entityManager.persist(moderatorToFindByMeetingNonSelfCreated);
        this.entityManager.persist(otherModeratorMeetingNonSelfCreated);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(moderatorToFindByMeetingSelfCreated, moderatorToFindByMeetingNonSelfCreated);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingRepository.findByModerator(moderatorToFindBy);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testFindByStatus_ShouldReturnOnlyMeetingsWithSpecifiedStatus() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting notStartedMeeting = buildMeeting("A&D Forum","Not Started", user, null);
        Meeting endedMeeting = buildMeeting("Technology Meeting","Ended", user, null);
        Meeting startedMeeting = buildMeeting("Strategy Meeting","Started", user, null);

        this.entityManager.persist(notStartedMeeting);
        this.entityManager.persist(endedMeeting);
        this.entityManager.persist(startedMeeting);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(notStartedMeeting);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingRepository.findByStatus("Not Started");

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    public Meeting buildMeeting(String name, String status, User user, User moderator){
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setStatus(status);
        meeting.setCreatedBy(user);
        meeting.setModerator(moderator);
        return meeting;
    }

    public User buildUser(String name){
        User user = new User();
        user.setName(name);
        return user;
    }
}