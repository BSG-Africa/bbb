package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.dto.MeetingInvite;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.model.User;
import za.co.bsg.services.MeetingManagementService;
import za.co.bsg.util.UtilService;
import za.co.bsg.util.UtilServiceImp;

import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    MeetingManagementService meetingManagementService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        appUser.setRole(UserRoleEnum.USER.toString());
        appUser.setBlocked(false);
        String passwordHashed = utilService.hashPassword(appUser.getPassword());
        appUser.setPassword(passwordHashed);
        return new ResponseEntity<User>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public @ResponseBody MeetingInvite joinInvite(@RequestParam String fullName, @RequestParam String meetingId) {
        String url = meetingManagementService.getInviteURL(fullName, meetingId);
        MeetingInvite invite = new MeetingInvite();
        invite.setInviteURL(url);
        invite.setFullName(fullName);
        return invite;
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
