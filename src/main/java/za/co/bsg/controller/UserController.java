package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.util.UtilService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UtilService utilService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> users() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> userById(@PathVariable Long id) {
        User appUser = userRepository.findOne(id);
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        User appUser = userRepository.findOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else if (appUser.getUsername().equalsIgnoreCase(loggedUsername)) {
            throw new RuntimeException("You cannot delete your account");
        } else {
            userRepository.delete(appUser);
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        String passwordHashed = utilService.hashPassword(appUser.getPassword());
        appUser.setPassword(passwordHashed);
        return new ResponseEntity<User>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public User updateUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null
                && userRepository.findUserByUsername(appUser.getUsername()).getId() != appUser.getId()) {
            throw new RuntimeException("Username already exist");
        }
        return userRepository.save(appUser);
    }

}
