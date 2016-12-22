package za.co.bsg.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.model.User;
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
public class UserControllerTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserController userController;

    @Autowired
    UtilService utilService;

    @Mock
    private SecurityContextHolder securityContextHolder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

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

    public User buildUser(String name, String username, String password){
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public SecurityContext createSecurityContext(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    public void Clear(List<User> users) {
        for (User user : users) {
            this.entityManager.remove(user);
        }
    }
}