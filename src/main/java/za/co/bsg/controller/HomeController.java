package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.co.bsg.repository.AppUserRepository;
import za.co.bsg.model.User;

import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private AppUserRepository appUserRepository;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (appUserRepository.findOneByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        appUser.setRole("USER");
        return new ResponseEntity<User>(appUserRepository.save(appUser), HttpStatus.CREATED);
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
