package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;

@Service
public class UserDataService {

    @Autowired
    UserRepository userRepository;

    public User findUserById(Long id){
        return userRepository.findOne(id);
    }
}
