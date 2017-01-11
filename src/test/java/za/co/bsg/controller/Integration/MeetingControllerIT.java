package za.co.bsg.controller.Integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.controller.MeetingController;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.MeetingManagementService;

import javax.persistence.EntityManager;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
public class MeetingControllerIT {

    @Autowired
    private MeetingController meetingController;
    @Autowired
    private EntityManager entityManager;

    @Mock
    private MeetingManagementService meetingManagementService;

    @Test
    public void createMeeting_ShouldReturnResponseEntityOfPersistedMeeting(){

        // Setup Fixtures
        User user = new User();
        user.setName("Candice Peterson");
        user.setUsername("Candice.Peterson@gmail.com");
        this.entityManager.persist(user);

        Meeting meetingToBeCreated = buildMeeting("Technology Meeting", user, user, MeetingStatusEnum.NotStarted.toString());

        // Set Expectation
        ResponseEntity<Meeting> expectedResponseEntity = new ResponseEntity<Meeting>(meetingToBeCreated, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<Meeting> actualResponseEntity = meetingController.createMeeting(meetingToBeCreated);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));

    }

    @Test
    public void editMeeting_ShouldReturnResponseEntityOfUpdatedMeeting() {

        // Setup Fixtures
        User user = new User();
        user.setName("Harry Peterson");
        user.setUsername("Harry.Peterson@gmail.com");

        User moderator = new User();
        moderator.setName("Tina Rosehope");
        moderator.setUsername("Tina.Rosehope@gmail.com");
        this.entityManager.persist(user);
        this.entityManager.persist(moderator);

        String status = MeetingStatusEnum.NotStarted.toString();
        Meeting existingDBMeetingAfterEdit = buildMeeting("Technology Meeting", user, moderator, status);
        Meeting existingDBMeetingToBeEdited = buildMeeting("A&D Meeting", user, user, status);
        this.entityManager.persist(existingDBMeetingToBeEdited);
        existingDBMeetingAfterEdit.setId(existingDBMeetingToBeEdited.getId());

        // Set Expectation
        ResponseEntity<Meeting> expectedResponseEntity = new ResponseEntity<Meeting>(existingDBMeetingAfterEdit, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<Meeting> actualResponseEntity = meetingController.editMeeting(existingDBMeetingAfterEdit);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));
    }

    @Test
    public void deleteMeetingWhereMeetingExists_ShouldReturnHttpStatusOkResponseEntityOfDeletedMeeting() {

        // Setup Fixtures
        User user = new User();
        user.setName("Harry Peterson");
        user.setUsername("Harry.Peterson@gmail.com");

        String status = MeetingStatusEnum.NotStarted.toString();
        Meeting meetingToBeDeleted = buildMeeting("A&D Meeting", user, null, status);
        this.entityManager.persist(meetingToBeDeleted);

        // Set Expectation
        ResponseEntity<Meeting> expectedResponseEntity = new ResponseEntity<Meeting>(meetingToBeDeleted, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<Meeting> actualResponseEntity = meetingController.deleteMeeting(meetingToBeDeleted.getId());

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));

    }

    @Test
    public void deleteMeetingByIdWhereMeetingDoesNotExist_ShouldReturnHttpStatusNoContentResponseEntityOfMeetingNotDeleted() {
        // Setup Fixtures
        long meetingToBeDeletedId = 3L;

        // Set Expectation
        ResponseEntity<Meeting> expectedResponseEntity = new ResponseEntity<Meeting>(HttpStatus.NO_CONTENT);

        // Exercise SUT
        ResponseEntity<Meeting> actualResponseEntity = meetingController.deleteMeeting(meetingToBeDeletedId);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));

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
