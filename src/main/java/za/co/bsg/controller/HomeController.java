package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.dto.MeetingInvite;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.services.MeetingManagementService;
import za.co.bsg.util.UtilService;

import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    MeetingManagementService meetingManagementService;

    /**
     * This method creates a user object in the user table
     * if user does not exist in the user table a RunTimeException is thrown
     * if user exists, a user role, blocked status and hashed password are set
     * then the user object is persisted in the user table.
     *
     * @param appUser a User data type - User object containing details for creating a
     *                user object in the user table
     * @return a ResponseEntity<User> containing persisted user object and a created HttpStatus
     */
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

    /**
     * This method generates a MeetingInvite that is used to join a meeting by
     * getting the invite url using bbb meeting fullName and meetingId
     * and meeting by meetingId, then using the the inviteUrl and meeting
     * to construct a MeetingInvite object
     *
     * @param fullName a String data type - a fullName of the bbb meeting
     *                 used to retrieve invite url
     * @param meetingId a String data type - a meetingId of a bbb meeting
     *                  used to retrieve a meeting
     * @return a MeetingInvite object
     */
    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public @ResponseBody MeetingInvite joinInvite(@RequestParam String fullName, @RequestParam String meetingId) {
        String url = meetingManagementService.getInviteURL(fullName, meetingId);
        Meeting meeting = meetingManagementService.getMeetingByMeetingId(meetingId);
        MeetingInvite invite = new MeetingInvite();
        invite.setInviteURL(url);
        invite.setFullName(fullName);
        invite.setMeetingStatus(meeting.getStatus());
        invite.setMeetingName(meeting.getName());

        return invite;
    }

    /**
     * This method returns a principal user
     *
     * @param principal a Principal data type - a principal user to be returned
     * @return a Principal object
     */
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
