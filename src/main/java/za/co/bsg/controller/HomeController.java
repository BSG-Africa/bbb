package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.dto.MeetingInvite;
import za.co.bsg.dto.PresentationUpload;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.services.MeetingManagementService;
import za.co.bsg.util.UtilService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    MeetingManagementService meetingManagementService;

    @Autowired
    private AppPropertiesConfiguration appPropertiesConfiguration;

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
        Meeting meeting = meetingManagementService.getMeetingByMeetingId(meetingId);
        MeetingInvite invite = new MeetingInvite();
        invite.setInviteURL(url);
        invite.setFullName(fullName);
        invite.setMeetingStatus(meeting.getStatus());
        invite.setMeetingName(meeting.getName());

        return invite;
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public PresentationUpload uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // TODO : Add correct url for BBB Implementation
        String appDirectory = request.getSession().getServletContext().getRealPath("/");
        System.out.println("App Directory: "+appDirectory);
        String homeDirectory = System.getProperty("user.home")+File.separator;
        System.out.println("Home Directory "+homeDirectory);
        System.out.println("URL: "+appPropertiesConfiguration.getUploadURL());

        try {
            file.transferTo(new File(homeDirectory  + file.getOriginalFilename()));
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PresentationUpload response = new PresentationUpload();
        response.setResponse("File added successfully");
        response.setUrl(homeDirectory  + file.getOriginalFilename());

        return response;
    }
}
