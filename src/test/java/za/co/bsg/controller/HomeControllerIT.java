package za.co.bsg.controller;

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
import za.co.bsg.util.UtilService;

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
    UtilService utilService;

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
       String username = "Non ExistingUser";
       userToBeCreated.setUsername(username);

        // Set Expectations
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setRole(UserRoleEnum.USER.toString());
        expectedUser.setBlocked(false);
        ResponseEntity<User> expectedCreatedUser = new ResponseEntity<User>(expectedUser, HttpStatus.CREATED);

        // Exercise SUT
        ResponseEntity<User> actualCreatedUser =  homeController.createUser(userToBeCreated);

        // Verify Behaviour ignoring password and Id as these are auto-generated
        assertThat(actualCreatedUser, is(sameBeanAs(expectedCreatedUser).ignoring("body.id").ignoring("body.password")));
    }

   @Test
    public void joinInvite_ShouldReturnMeetingInvite(){
       // Setup Fixtures
       String  meetingId= "dfe32fgdf";
       String status = "Started";
       String fullName = "A&D Meeting";
       Meeting meetingToJoin = new Meeting();
       meetingToJoin.setStatus(status);
       meetingToJoin.setName(fullName);
       meetingToJoin.setMeetingId(meetingId);
       entityManager.persist(meetingToJoin);

       // Set Expectations
       MeetingInvite expectedMeetingInvite = new MeetingInvite();
       expectedMeetingInvite.setFullName(fullName);
       expectedMeetingInvite.setMeetingName(fullName);
       expectedMeetingInvite.setMeetingStatus(status);

       // Exercise SUT
       MeetingInvite actualMeetingInvite = homeController.joinInvite(fullName, meetingId);
       expectedMeetingInvite.setInviteURL(actualMeetingInvite.getInviteURL());

       // Verify Behaviour
       assertThat(actualMeetingInvite, is(sameBeanAs(expectedMeetingInvite)));

    }

}
