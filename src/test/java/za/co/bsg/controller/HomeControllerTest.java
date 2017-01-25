package za.co.bsg.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;

import java.io.IOException;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class HomeControllerTest {

    @Mock
    private UserRepository userRepository;
    @Autowired
    private HomeController homeController;

    @Test
    public void testCreateUserWhereUsernameIsNotNull_ShouldThrowRuntimeException() throws IOException {
        // Setup Fixture
        User userToBeCreated = buildUser("Sean Storm", "John@gmail.com", "password1");
        User existingUser = buildUser("Sean Peters", "Sean@gmail.com", "password1");

        // Set Expectation
        when(userRepository.findUserByUsername(userToBeCreated.getUsername())).thenReturn(existingUser);
        RuntimeException expectedRuntimeException = new RuntimeException("Username already exist");

        // Exercise SUT and Verify
        try {
            homeController.createUser(userToBeCreated);
        }catch (RuntimeException actualRuntimeException){
            assertThat(actualRuntimeException, is(sameBeanAs(expectedRuntimeException)));
        }

    }

    @Test
    public void testCreateUserWhereUsernameIsNull_ShouldCReturnResponseEntity()  {
        // Setup Fixture
        User userToBeCreated = buildUser("Philip Storm", "Philip.Storm@gmail.com", "password1");

        // Set Expectations
        when(userRepository.findUserByUsername(userToBeCreated.getUsername())).thenReturn(null);
        ResponseEntity<User> expectedResponseEntity = new ResponseEntity<User>(userToBeCreated, HttpStatus.CREATED);

        // Exercise SUT
        ResponseEntity<User> actualResponseEntity = homeController.createUser(userToBeCreated);

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
}