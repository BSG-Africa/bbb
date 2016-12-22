package za.co.bsg.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.bsg.model.User;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserDataServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDataService userDataService;

    @Test
    public void testFindByUserId_ShouldOnlyReturnSpecifiedUser(){
        // Setup Fixtures
        User userToBeFound= buildUser("Helen Jones");
        User user= buildUser("James Roos");

        this.entityManager.persist(userToBeFound);
        this.entityManager.persist(user);

        // Set Expectations
        User expectedUser = userToBeFound;
        userDataService.findUserById(userToBeFound.getId());

        // Exercise SUT
        User actualUser =  userDataService.findUserById(userToBeFound.getId());

        // Verify
        assertThat(actualUser, is(sameBeanAs(expectedUser)));
    }

    public User buildUser(String name){
        User user = new User();
        user.setName(name);
        return user;
    }

}