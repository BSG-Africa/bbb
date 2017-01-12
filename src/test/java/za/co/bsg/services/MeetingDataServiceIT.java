package za.co.bsg.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.enums.MeetingStatusEnum;
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
public class MeetingDataServiceIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeetingDataService meetingDataService;

    @Test
    public void retrieveAllByUserId_ShouldReturnOnlyMeetingsForRelevantUser(){
        // Setup Fixtures
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();
        User relevantUser = buildUser("Helen Rose");
        User irrelevantUser = buildUser("Deon Smith");
        this.entityManager.persist(relevantUser);
        this.entityManager.persist(irrelevantUser);

        Meeting relevantUserMeeting = buildMeeting("A&D Forum",meetingStatus, relevantUser, null);
        Meeting irrelevantUserMeeting = buildMeeting("Technology Meeting",meetingStatus, irrelevantUser, null);
        this.entityManager.persist(relevantUserMeeting);
        this.entityManager.persist(irrelevantUserMeeting);

        // Set Expectations
        List<Meeting> expectedMeetings = singletonList(relevantUserMeeting);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByUserId(relevantUser);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void retrieveAllByModerator_ShouldOnlyReturnMeetingsWhereUserIsModerator() {
        // Setup Fixtures
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();
        User moderatorUser= buildUser("Helen Jones");
        User nonModeratorUser= buildUser("Deon Smith");
        this.entityManager.persist(moderatorUser);
        this.entityManager.persist(nonModeratorUser);

        Meeting meetingCreatedByModeratorModeratedByModerator = buildMeeting("A&D Forum",meetingStatus, moderatorUser, moderatorUser);
        Meeting meetingCreatedByNonModeratorModeratedByModerator = buildMeeting("A&D Forum",meetingStatus, nonModeratorUser, moderatorUser);
        Meeting meetingCreatedByModeratorModeratedByNonModerator = buildMeeting("A&D Forum",meetingStatus, moderatorUser, nonModeratorUser);

        this.entityManager.persist(meetingCreatedByModeratorModeratedByModerator);
        this.entityManager.persist(meetingCreatedByNonModeratorModeratedByModerator);
        this.entityManager.persist(meetingCreatedByModeratorModeratedByNonModerator);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(meetingCreatedByModeratorModeratedByModerator, meetingCreatedByNonModeratorModeratedByModerator);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByModerator(moderatorUser);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void retrieveAllByStatus_ShouldReturnOnlyMeetingsWithSpecifiedStatus() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);
        String notStartedMeetingStatus = MeetingStatusEnum.NotStarted.toString();
        String endedMeetingStatus = MeetingStatusEnum.Ended.toString();
        String startedMeetingStatus = MeetingStatusEnum.Started.toString();

        Meeting notStartedMeeting = buildMeeting("A&D Forum",notStartedMeetingStatus, user, null);
        Meeting endedMeeting = buildMeeting("Technology Meeting",endedMeetingStatus, user, null);
        Meeting startedMeeting = buildMeeting("Strategy Meeting",startedMeetingStatus, user, null);

        this.entityManager.persist(notStartedMeeting);
        this.entityManager.persist(endedMeeting);
        this.entityManager.persist(startedMeeting);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(notStartedMeeting);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingDataService.retrieveAllByStatus(notStartedMeetingStatus);

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void retrieveAll_ShouldReturnAllMeetings() {
        // Setup Fixtures
        User user1= buildUser("Helen Jones");
        User user2= buildUser("Helen Smith");
        this.entityManager.persist(user1);
        this.entityManager.persist(user2);
        String notStartedMeetingStatus = MeetingStatusEnum.NotStarted.toString();
        String endedMeetingStatus = MeetingStatusEnum.Ended.toString();
        String startedMeetingStatus = MeetingStatusEnum.Started.toString();

        Meeting meeting1 = buildMeeting("A&D Forum",notStartedMeetingStatus, user1, null);
        Meeting meeting2 = buildMeeting("Technology Meeting",endedMeetingStatus, user1, null);
        Meeting meeting3 = buildMeeting("Strategy Meeting",startedMeetingStatus, user2, null);

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
    public void retrieve_ShouldOnlyReturnSpecifiedMeeting() {
        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);
        String notStartedMeetingStatus = MeetingStatusEnum.NotStarted.toString();
        String endedMeetingStatus = MeetingStatusEnum.Ended.toString();

        Meeting specifiedMeeting = buildMeeting("A&D Forum",notStartedMeetingStatus, user, null);
        Meeting otherMeeting = buildMeeting("Technology Meeting",endedMeetingStatus, user, null);

        this.entityManager.persist(specifiedMeeting);
        this.entityManager.persist(otherMeeting);

        // Set Expectations
        Meeting expectedMeeting = specifiedMeeting;

        // Exercise SUT
        Meeting actualMeeting = meetingDataService.retrieve(specifiedMeeting.getId());

        // Verify
        assertThat(actualMeeting, is(sameBeanAs(expectedMeeting)));
    }

    @Test
    public void union_ShouldJoinListsAndReturnResult(){
        // Setup Fixture
        User user= buildUser("Helen Jones");
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();

        Meeting meetingToBeSaved1 = buildMeeting("A&D Forum",meetingStatus, user, null);
        Meeting meetingToBeSaved2 = buildMeeting("Technology Forum",meetingStatus, user, null);
        Meeting meetingToBeSaved3 = buildMeeting("Strategy Forum",meetingStatus, user, null);

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
    public void save_ShouldSaveAndReturnSavedMeeting() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();

        Meeting meetingToBeSaved = buildMeeting("A&D Forum",meetingStatus, user, null);

        // Set Expectations
        Meeting expectedMeeting = meetingToBeSaved;

        // Exercise SUT
        meetingDataService.save(meetingToBeSaved);
        Meeting actualMeeting = meetingDataService.retrieve(meetingToBeSaved.getId());

        // Verify
        assertThat(actualMeeting, is(sameBeanAs(expectedMeeting)));
    }

    @Test
    public void save_ShouldSaveAndReturnMultipleMeetingsSaved(){

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();

        Meeting meetingToBeSaved1 = buildMeeting("A&D Forum",meetingStatus, user, null);
        Meeting meetingToBeSaved2 = buildMeeting("Technology Forum",meetingStatus, user, null);
        Meeting meetingToBeSaved3 = buildMeeting("Strategy Forum",meetingStatus, user, null);

        // Set Expectations
        List<Meeting> expectedMeetings = asList(meetingToBeSaved1, meetingToBeSaved2, meetingToBeSaved3);

        // Exercise SUT
        List<Meeting> actualMeetings = meetingDataService.save(asList(meetingToBeSaved1, meetingToBeSaved2, meetingToBeSaved3));;

        // Verify
        assertThat(actualMeetings, is(sameBeanAs(expectedMeetings)));
    }

    @Test
    public void delete_ShouldDeleteSpecifiedMeeting() {

        // Setup Fixtures
        User user= buildUser("Helen Jones");
        this.entityManager.persist(user);
        String meetingStatus = MeetingStatusEnum.NotStarted.toString();

        Meeting meetingToBeDeleted = buildMeeting("A&D Forum",meetingStatus, user, null);
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
