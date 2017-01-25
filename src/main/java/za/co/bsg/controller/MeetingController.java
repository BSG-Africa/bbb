package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.dto.PresentationUpload;
import za.co.bsg.model.Meeting;
import za.co.bsg.services.MeetingManagementService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MeetingController {

    private MeetingManagementService meetingManagementService;

    @Autowired
    private AppPropertiesConfiguration appPropertiesConfiguration;

    @Autowired
    public MeetingController(MeetingManagementService meetingManagementService){
        this.meetingManagementService = meetingManagementService;
    }

    /**
     * This method retrieves meetings from the meeting table by a userId
     *
     * @param userId s long data type - a userId used to retrieve user meetings by
     * @return a list of Meeting object
     */
    @RequestMapping(value = "/meeting/{userId}", method = RequestMethod.GET)
    public List<Meeting> userMeetings(@PathVariable("userId") long userId){
        return meetingManagementService.getMeetingsByUser(userId);
    }

    /**
     * This method retrieves meetings available for a user by filtering out meetings with specified userId
     *
     * @param userId a long data type - a userId used to retrieve available meetings by
     * @return as list of Meeting objects
     */
    @RequestMapping(value = "/meeting/available/{userId}", method = RequestMethod.GET)
    public @ResponseBody List<Meeting> availableMeetings(@PathVariable("userId") long userId) {
        return meetingManagementService.getAllMeetings(userId);
    }

    /**
     * This method creates a meeting in the meeting table using the meeting object parsed,
     * if a meeting creation is unsuccessful a UnsupportedEncodingException exception
     *
     * @param meeting a Meeting object data type - a meeting object used to create a meeting
     * @return a ResponseEntity<Meeting> containing persisted meeting object and OK HttpStatus
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/meeting/create", method = RequestMethod.POST)
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = new Meeting();
        try {
            persistedMeeting = meetingManagementService.createMeeting(meeting);
        } catch (UnsupportedEncodingException e) {
            // TODO : Can a more meaningful exception be thrown?
            e.printStackTrace();
        }
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }

    /**
     * This method edits a meeting that exists in the meeting table using the
     * meeting object parsed as a parameter.
     *
     * @param meeting a Meeting object data type - a meeting object used to edit an existing meeting
     * @return a ResponseEntity <Meeting>  containing edited meeting and OK HttpStatus
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/meeting/edit", method = RequestMethod.POST)
    public ResponseEntity<Meeting> editMeeting(@RequestBody Meeting meeting) {
        Meeting persistedMeeting = meetingManagementService.editMeeting(meeting);
        return new ResponseEntity<Meeting>(persistedMeeting, HttpStatus.OK);
    }

    /**
     * This method deletes a meeting in the meeting table using the meeting id
     *
     * @param id a Long data type - meeting id used to delete a meeting
     * @return a ResponseEntity of Meeting object
     */
    @RequestMapping(value = "/meeting/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Meeting> deleteMeeting(@PathVariable Long id) {
        return meetingManagementService.deleteMeeting(id);
    }

    /**
     * This Method gets a meeting in the meeting table using the meeting id
     *
     * @param id a Long data type - which is used to get a meeting by
     * @return a Meeting object
     */
    @RequestMapping(value = "/meeting/retrieve/{id}", method = RequestMethod.GET)
    public Meeting getMeeting(@PathVariable Long id) {
        return meetingManagementService.getMeeting(id);
    }

    /**
     * This methods encodes the upload file to a format that is uploaded as pert of creating
     * or editing of a meeting by getting the the upload directory and encoding a url for uploaded file.
     * A presentation upload response is then set using the encoded url
     *
     * @param file a MultipartFile object data type - Which is a presentation to be uploaded
     *             when creating or editing a bbb meeting
     * TODO: Tiyani : to remove @param request after confirming usage
     * @return a PresentationUpload object
     */
    @RequestMapping(value = "/meeting/upload", method = RequestMethod.POST)
    @ResponseBody
    public PresentationUpload uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // TODO: Tiyani: Confirm request usag
        // String appDirectory = request.getSession().getServletContext().getRealPath("/");
        String uploadDirectory;
        String filename = "";
        String url = "";
        if("~".equals(appPropertiesConfiguration.getUploadPath())){
            uploadDirectory = System.getProperty("user.home")+ File.separator;
        } else {
            uploadDirectory = appPropertiesConfiguration.getUploadPath();
        }

        try {
            filename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "");
            file.transferTo(new File(uploadDirectory  + filename));
            url = appPropertiesConfiguration.getUploadURL()  + URLEncoder.encode(filename, "UTF-8");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PresentationUpload response = new PresentationUpload();
        response.setResponse("File added successfully");
        response.setUrl(url);

        return response;
    }
}
