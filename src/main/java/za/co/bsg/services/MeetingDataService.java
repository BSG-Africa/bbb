package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.repository.MeetingRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MeetingDataService {

    @Autowired
    MeetingRepository meetingRepository;

    /**
     * This method retrieves all meetings in the meeting table created by the current user logged on
     *
     * @param user a User data type - Which is the user to retrieve meeting by
     * @return a list of Meeting objects
     */
    public List<Meeting> retrieveAllByUserId(User user) {
        return meetingRepository.findByCreatedBy(user);
    }

    /**
     * This method retrieves all meetings in the meeting table moderated by the current user logged on
     *
     * @param moderator a User data type - Which is the meeting moderator to retrieve a meeting by
     * @return a list of Meeting objects
     */
    public List<Meeting> retrieveAllByModerator(User moderator) {
        return meetingRepository.findByModerator(moderator);
    }

    /**
     * This method unions two lists and a list containing elements from both lists
     *
     * @param list1  a List<T> data type - Which is the list to be unified with list2
     * @param list2  a List<T> data type - Which is the list to be unified with list1
     * @return a list of T objects
     */
    public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    /**
     * This method persists a meeting to the meeting table
     *
     * @param meeting  a Meeting data type - Which is the meeting object to be persisted
     *                 to the meeting table
     * @return a Meeting object
     */
    public Meeting save(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    /**
     * This method persists meeting objects in list to the meeting table
     *
     * @param meetings  a List<Meeting> data type - Which is the list of meting objects to be persisted
     *                  to the meeting table
     * @return  a list of Meeting objects
     */
    public List<Meeting> save(List<Meeting> meetings) {
        return meetingRepository.save(meetings);
    }

    public void delete(Long meetingId) {
        meetingRepository.delete(meetingId);
    }

    /**
     * This method retrieves a meeting object in the meeting table that matches the
     * meetingId
     *
     * @param meetingId a Long data type - Which is the meeting id to retrieve by
     * @return a Meeting object
     */
    public Meeting retrieve(Long meetingId) {
        return meetingRepository.findOne(meetingId);
    }

    /**
     * This method retrieves a meeting object in the meeting table that matches the meetingId
     *
     * @param meetingId a String data type - Which is the bbb meeting id to retrieve by
     * @return a Meeting object
     */
    public Meeting retrieveByMeetingId(String meetingId) {
        return meetingRepository.findByMeetingId(meetingId).get(0);
    }

    /**
     * This method retrieves all meetings in the meeting table that contain status to retrieve by
     *
     * @param status a String data type - Which is the status of a meeting to retrieve by
     * @return  a list of Meeting objects
     */
    public List<Meeting> retrieveAllByStatus(String status) {
        return meetingRepository.findByStatus(status);
    }

    /**
     * This method retrieves all meetings in the meeting table that don't contain the status to be excluded
     *
     * @param status a String data type - Which is the status of a meeting to be excluded
     * @return a list of Meeting objects
     */
    public List<Meeting> retrieveAllExcludeStatus(String status) {
        return meetingRepository.findByStatusNot(status);
    }
}
