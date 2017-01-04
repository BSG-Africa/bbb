package za.co.bsg.controller;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import za.co.bsg.dto.MeetingInvite;
import za.co.bsg.dto.PresentationUpload;
import za.co.bsg.enums.MeetingStatusEnum;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.Meeting;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.model.User;
import za.co.bsg.services.MeetingManagementService;
import za.co.bsg.services.api.exception.BigBlueButtonException;
import za.co.bsg.util.UtilService;
import za.co.bsg.util.UtilServiceImp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Iterator;

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

        String appDirectory = request.getSession().getServletContext().getRealPath("/");
        System.out.println("Path: "+appDirectory);
        String rootDirectory = "D:\\";
        System.out.println("Root Directory "+rootDirectory);
        try {
            file.transferTo(new File(rootDirectory  + file.getOriginalFilename()));
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PresentationUpload response = new PresentationUpload();
        response.setResponse("File added successfully");
        response.setUrl(rootDirectory  + file.getOriginalFilename());

        return response;
    }
}
