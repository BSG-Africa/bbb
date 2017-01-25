package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;

import java.util.List;


@Service
public class UserDataService {

    @Autowired
    UserRepository userRepository;

    /**
     * This method retrieves a user object from the user table by id
     *
     * @param id a Long data type - Which is the id to retrieve user by
     * @return  a User object
     */
    public User findUserById(Long id){
        return userRepository.findOne(id);
    }

    /**
     * This method retrieves a user object from the user table by username
     *
     * @param username a String data type - Which is the username to retrieve user by
     * @return  a User object
     */
    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    /**
     * This method persists a user to the user table
     *
     * @param user  a User data type - Which is the user object to be persisted
     *                 to the user table
     * @return User object
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * This method delete the user from table
     */
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * This method retrieves all users
     *
     * @return a list of User objects
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
