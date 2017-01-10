package za.co.bsg.controller.Integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.controller.HomeController;
import za.co.bsg.dto.MeetingInvite;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import javax.persistence.EntityManager;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
public class HomeControllerIT {

   @Autowired
   private EntityManager entityManager;

   @Autowired
   private HomeController homeController;

   @Test
   public void createUserWhereUserExists_ShouldThrowRuntimeException() {

        // Setup Fixtures
        User userToBeCreated = new User();
        userToBeCreated.setUsername("Existing User");
        entityManager.persist(userToBeCreated);

        // Set Expectations
        RuntimeException expectedRuntimeException = new RuntimeException("Username already exist");

        // Exercise SUT
        RuntimeException actualRuntimeException = new RuntimeException();
        try {
            homeController.createUser(userToBeCreated);
        }catch (RuntimeException e){
            actualRuntimeException = e;
        }

        // Verify Behaviour
        assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
    }

   @Test
   public void createUserWhereUserDoesNotExist_ShouldCreateUser() {

        // Setup Fixtures
        User userToBeCreated = new User();
        userToBeCreated.setUsername("Non ExistingUser");

        // Set Expectations
        User expectedUser = new User();
        expectedUser.setUsername("Non ExistingUser");
        expectedUser.setRole(UserRoleEnum.USER.toString());
        expectedUser.setPassword("password");
        expectedUser.setBlocked(false);
        ResponseEntity<User> expectedCreatedUser = new ResponseEntity<User>(expectedUser, HttpStatus.CREATED);

        // Exercise SUT
        ResponseEntity<User> actualCreatedUser =  homeController.createUser(userToBeCreated);
        expectedCreatedUser.getBody().setPassword(actualCreatedUser.getBody().getPassword());
        expectedCreatedUser.getBody().setId(actualCreatedUser.getBody().getId());

        // Verify Behaviour
        assertThat(actualCreatedUser, is(sameBeanAs(expectedCreatedUser)));
    }

   @Test
    public void joinInvite_ShouldReturnMessageInvite(){

       // Setup Fixtures
       String  meetingId= "dfe32fgdf";
       Meeting meetingToJoin = new Meeting();
       meetingToJoin.setStatus("Started");
       meetingToJoin.setName("A&D Meeting");
       meetingToJoin.setMeetingId(meetingId);
       entityManager.persist(meetingToJoin);

       // Set Expectations
       MeetingInvite expectedMeetingInvite = new MeetingInvite();
       expectedMeetingInvite.setFullName("A&D Meeting");
       expectedMeetingInvite.setMeetingName("A&D Meeting");
       expectedMeetingInvite.setMeetingStatus("Started");

       // Exercise SUT
       String fullName = "A&D Meeting";
       MeetingInvite actualMeetingInvite = homeController.joinInvite(fullName, meetingId);
       expectedMeetingInvite.setInviteURL(actualMeetingInvite.getInviteURL());

       // Verify Behaviour
       assertThat(actualMeetingInvite, is(sameBeanAs(expectedMeetingInvite)));

    }

}
