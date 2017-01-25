package za.co.bsg.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.BigBlueButtonApplication;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.User;
import za.co.bsg.resources.CustomUserDetailsTestUtil;
import za.co.bsg.util.UtilService;

import javax.persistence.EntityManager;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(classes = { BigBlueButtonApplication.class, TestConfiguration.class })
public class UserControllerTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserController userController;

    @Autowired
    UtilService utilService;

    private CustomUserDetailsTestUtil customUserDetails;

    private SecurityContextHolder securityContextHolder;

    @Test
    public void testUsers_ShouldReturnListOfExistingUsers() throws Exception {


        // Setup Fixture
        Clear(userController.users());
        User adminUser = buildUser("Cynthia Jones", "Cynthia.Jones@gmail.com", "password1");
        User nonAdminUser = buildUser("Sam Storm", "SamS@gmail.com", "password2");
        this.entityManager.persist(adminUser);
        this.entityManager.persist(nonAdminUser);

        // Set Expectations
        List<User> expectedUsers = asList(adminUser, nonAdminUser);

        // Exercise SUT
        List<User> actualUsers = userController.users();

        // Verify
        assertThat(actualUsers, is(sameBeanAs(expectedUsers)));
    }

    @Test
    public void testUserByIdWhereUserDoesNotExist_ShouldReturnNoContentStatus()  {
        // Setup Fixture
        long userToBeFoundId =88L;

        // Set Expectation
        ResponseEntity<User> expectedResponseEntity =  new ResponseEntity<User>(HttpStatus.NO_CONTENT);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = userController.userById(userToBeFoundId);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));
    }

    @Test
    public void testUserByIdWhereUserExists_ShouldReturnUserAndOkStatus()  {
        // Setup Fixture
        User userToBeFound = buildUser("John Stoltz" , "StoltzJ@gmail.com", "password1");
        this.entityManager.persist(userToBeFound);
        long userToBeFoundId = userToBeFound.getId();

        // Set Expectation
        ResponseEntity<User> expectedResponseEntity =  new ResponseEntity<User>(userToBeFound, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = userController.userById(userToBeFoundId);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));
    }

    @Test
    public void testCreateUserWhereUserDoesNotExist_ShouldThrowRuntimeException() {
        // Setup Fixture
        User userToBeCreated = buildUser("Stephan Peters", "Stephan@gmail.com", "password1");

        // Set Expectation
        ResponseEntity<User> expectedResponseEntity = new ResponseEntity<User>(userToBeCreated, HttpStatus.CREATED);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = userController.createUser(userToBeCreated);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));

    }

    @Test
    public void testCreateUserWhereUserExists_ShouldReturnResponseBodyWithCreatedUserAndCreatedStatus() {
        // Setup Fixture
        User userToBeCreated = buildUser("Ester Peters", "Ester@gmail.com", "password1");
        this.entityManager.persist(userToBeCreated);

        // Set Expectation
        RuntimeException expectedRuntimeException = new RuntimeException("Username already exists");

        // Exercise SUT and Verify
        try {
            userController.createUser(userToBeCreated);
        }catch (RuntimeException actualRuntimeException){
            assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
        }
    }

    @Test
    public void testEditUserWhereUserExistsAndUserIdIsDifferent_ShouldThrowRuntimeException(){

        // Setup Fixture
        User existingUser = buildUser("John Peters", "JohnP@gmail.com", "password1");
        this.entityManager.clear();
        this.entityManager.persist(existingUser);

        User userToBeEdited = buildUser("John Storm", "JohnS@gmail.com", "password1");

        // Set Expectation
        RuntimeException expectedRuntimeException = new RuntimeException("Username already exists");

        // Exercise SUT and Verify
        try {
            userController.createUser(userToBeEdited);
        }catch (RuntimeException actualRuntimeException){
            assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
        }
    }

    @Test
    public void testDeleteUserWhereDoesNotExist_ShouldReturnNoContentResponseEntity() {

        // Setup Fixture
        long userToBeDeletedId = 301L;
        User user = new User();
        user.setUsername("User");
        user.setRole(UserRoleEnum.ADMIN.toString());

        initContext(user);

        // Set Expectation
        ResponseEntity<User> expectedResponseEntity = new ResponseEntity<User>(HttpStatus.NO_CONTENT);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = userController.deleteUser(userToBeDeletedId);

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));
    }

    @Test
    public void testDeleteUserWhereUserCurrentlyLoggedOn_ShouldThrowRuntimeExceptionUserCantDeleteAccount() {

        // Setup Fixture
        User userToBeDeleted = new User();
        userToBeDeleted.setUsername("Zanele@abc.org");
        userToBeDeleted.setRole(UserRoleEnum.ADMIN.toString());
        entityManager.persist(userToBeDeleted);

        User loggedOnUser = new User();
        loggedOnUser.setUsername("Zanele@abc.org");
        loggedOnUser.setRole(UserRoleEnum.ADMIN.toString());
        initContext(loggedOnUser);

        // Set Expectation
         RuntimeException expectedRuntimeException  = new RuntimeException("You cannot delete your account");

        // Exercise SUT
        RuntimeException actualRuntimeException  = new RuntimeException("");
        try {
            userController.deleteUser(userToBeDeleted.getId());
        }catch (RuntimeException e){
            actualRuntimeException = e;
        }

        // Verify
        assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
    }


    @Test
    public void testDeleteUserWhereUserRoleIsAdmin_ShouldThrowRuntimeExceptionUserCantDeleteAdminAccount() {

        // Setup Fixture
        User userToBeDeleted = new User();
        userToBeDeleted.setUsername("Zanele@abc.org");
        userToBeDeleted.setRole(UserRoleEnum.ADMIN.toString());
        entityManager.persist(userToBeDeleted);

        User loggedOnUser = new User();
        loggedOnUser.setUsername("Lucia@abc.org");
        loggedOnUser.setRole(UserRoleEnum.ADMIN.toString());
        initContext(loggedOnUser);

        // Set Expectation
        RuntimeException expectedRuntimeException  = new RuntimeException("You cannot delete an admin account");

        // Exercise SUT
        RuntimeException actualRuntimeException  = new RuntimeException("");
        try {
            userController.deleteUser(userToBeDeleted.getId());
        }catch (RuntimeException e){
            actualRuntimeException = e;
        }

        // Verify
        assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
    }

    @Test
    public void testDeleteUserIsNotLoggedOnAndUserRoleIsUser_ShouldDeleteUser() {

        // Setup Fixture
        User userToBeDeleted = new User();
        userToBeDeleted.setUsername("Zanele@abc.org");
        userToBeDeleted.setRole(UserRoleEnum.USER.toString());
        entityManager.persist(userToBeDeleted);

        User loggedOnUser = new User();
        loggedOnUser.setUsername("Lucia@abc.org");
        loggedOnUser.setRole(UserRoleEnum.ADMIN.toString());
        initContext(loggedOnUser);

        // Set Expectation
        ResponseEntity<User> expectedResponseEntity  = new ResponseEntity<User> (userToBeDeleted, HttpStatus.OK);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = userController.deleteUser(userToBeDeleted.getId());

        // Verify
        assertThat(actualResponseEntity, is(sameBeanAs(expectedResponseEntity)));
    }

    public User buildUser(String name, String username, String password){
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    protected void initContext(User user) {
        customUserDetails = new CustomUserDetailsTestUtil();
        securityContextHolder = new SecurityContextHolder();

        UserDetails userDetails =  customUserDetails.loadUserByUsername(user);
        Authentication authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ;
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void Clear(List<User> users) {
        for (User user : users) {
            this.entityManager.remove(user);
        }
    }
}