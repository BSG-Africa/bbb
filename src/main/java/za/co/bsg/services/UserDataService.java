package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;

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
}
