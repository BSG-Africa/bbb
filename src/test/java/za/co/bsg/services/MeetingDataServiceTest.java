package za.co.bsg.services;

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
public class MeetingDataServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeetingDataService meetingDataService;

    @Test
    public void testRetrieveAllByUserId_ShouldReturnOnlyMeetingsForUser(){
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
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByUserId(userToFindMeetingBy);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testRetrieveAllByModerator_ShouldOnlyReturnMeetingsUserIsModeratorOf() {
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
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByModerator(moderatorToFindBy);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testRetrieveAllByStatus_ShouldReturnOnlyMeetingsWithSpecifiedStatus() {

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
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByStatus("Not Started");

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testRetrieveAll_ShouldReturnAllMeetings() {
        // Setup Fixtures
        User user1= buildUser("Helen Jones");
        User user2= buildUser("Helen Jones");
        this.entityManager.persist(user1);
        this.entityManager.persist(user2);

        Meeting meeting1 = buildMeeting("A&D Forum","Not Started", user1, null);
        Meeting meeting2 = buildMeeting("Technology Meeting","Ended", user1, null);
        Meeting meeting3 = buildMeeting("Strategy Meeting","Started", user2, null);

        this.entityManager.persist(meeting1);
        this.entityManager.persist(meeting2);
        this.entityManager.persist(meeting3);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(meeting1, meeting2, meeting3);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingDataService.retrieveAll();

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testRetrieve_ShouldOnlyReturnSpecifiedMeeting() {
        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting meetingToFind = buildMeeting("A&D Forum","Not Started", user, null);
        Meeting otherMeeting = buildMeeting("Technology Meeting","Ended", user, null);

        this.entityManager.persist(meetingToFind);
        this.entityManager.persist(otherMeeting);

        // Set Expectations
        Meeting expectedMeeting = meetingToFind;

        // Exercise SUT
        Meeting actualMeeting = meetingDataService.retrieve(meetingToFind.getId());

        // Verify
        assertThat(actualMeeting, is(sameBeanAs(expectedMeeting)));
    }

    @Test
    public void testUnion_ShouldReturnResultSetContainingDataOfTwoList(){
        // Setup Fixture
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting meetingToBeSaved1 = buildMeeting("A&D Forum","Not Started", user, null);
        Meeting meetingToBeSaved2 = buildMeeting("Technology Forum","Not Started", user, null);
        Meeting meetingToBeSaved3 = buildMeeting("Strategy Forum","Not Started", user, null);

        List<Meeting> meetingsList1 = asList(meetingToBeSaved1);
        List<Meeting> meetingsList2 = asList(meetingToBeSaved2, meetingToBeSaved3);

        // Set Expectations
        List<Meeting> expectedUnionResults = asList(meetingToBeSaved1,meetingToBeSaved2,  meetingToBeSaved3);

        // Exercise SUT
        List<Meeting> actualUnionResults = meetingDataService.union(meetingsList1, meetingsList2);

        // Verify
        assertThat(actualUnionResults, is(sameBeanAs(expectedUnionResults).ignoring("name")));
    }

    @Test
    public void testSave_ShouldSaveAndReturnMeetingSaved() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting meetingToBeSaved = buildMeeting("A&D Forum","Not Started", user, null);

        // Set Expectations
        Meeting expectedMeeting = meetingToBeSaved;

        // Exercise SUT
        meetingDataService.save(meetingToBeSaved);
        Meeting actualMeeting = meetingDataService.retrieve(meetingToBeSaved.getId());

        // Verify
        assertThat(actualMeeting, is(sameBeanAs(expectedMeeting)));
    }

    @Test
    public void testSave_ShouldSaveAndReturnMultipleMeetingsSaved(){

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting meetingToBeSaved1 = buildMeeting("A&D Forum","Not Started", user, null);
        Meeting meetingToBeSaved2 = buildMeeting("Technology Forum","Not Started", user, null);
        Meeting meetingToBeSaved3 = buildMeeting("Strategy Forum","Not Started", user, null);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(meetingToBeSaved1, meetingToBeSaved2, meetingToBeSaved3);

        // Exercise SUT
        meetingDataService.save(asList(meetingToBeSaved1, meetingToBeSaved2, meetingToBeSaved3));
        List<Meeting> actualMeetings = meetingDataService.retrieveAll();

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void testDelete_ShouldDeleteSpecifiedMeeting() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);

        Meeting meetingToBeDeleted = buildMeeting("A&D Forum","Not Started", user, null);
        Meeting otherMeeting = buildMeeting("Technology Meeting","Ended", user, null);

        this.entityManager.persist(meetingToBeDeleted);
        this.entityManager.persist(otherMeeting);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(otherMeeting);

        // Exercise SUT
        meetingDataService.delete(meetingToBeDeleted.getId());
        List<Meeting> actualMeetings = meetingDataService.retrieveAll();

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
