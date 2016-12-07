package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.model.User;
import za.co.bsg.util.UtilServiceImp;

import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    UtilServiceImp utilServiceImp;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        appUser.setRole(UserRoleEnum.USER.toString());
        appUser.setBlocked(false);
        String passwordHashed = utilServiceImp.hashPassword(appUser.getPassword());
        appUser.setPassword(passwordHashed);
        return new ResponseEntity<User>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
