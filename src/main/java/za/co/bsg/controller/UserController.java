package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.repository.AppUserRepository;
import za.co.bsg.model.User;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    private AppUserRepository appUserRepository;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> users() {
        return appUserRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> userById(@PathVariable Long id) {
        User appUser = appUserRepository.findOne(id);
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        User appUser = appUserRepository.findOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else if (appUser.getUsername().equalsIgnoreCase(loggedUsername)) {
            throw new RuntimeException("You cannot delete your account");
        } else {
            appUserRepository.delete(appUser);
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (appUserRepository.findOneByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        return new ResponseEntity<User>(appUserRepository.save(appUser), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public User updateUser(@RequestBody User appUser) {
        if (appUserRepository.findOneByUsername(appUser.getUsername()) != null
                && appUserRepository.findOneByUsername(appUser.getUsername()).getId() != appUser.getId()) {
            throw new RuntimeException("Username already exist");
        }
        return appUserRepository.save(appUser);
    }

}
