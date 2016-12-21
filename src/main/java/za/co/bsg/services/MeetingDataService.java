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

    public List<Meeting> retrieveAllByUserId(User user) {
        return meetingRepository.findByCreatedBy(user);
    }

    public List<Meeting> retrieveAllByModerator(User moderator) {
        return meetingRepository.findByModerator(moderator);
    }

    public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    public Meeting save(Meeting meeting) {
        return meetingRepository.save(meeting);
    }


    public List<Meeting> save(List<Meeting> meetings) {
        return meetingRepository.save(meetings);
    }

    public List<Meeting> retrieveAll() {
        return meetingRepository.findAll();
    }

    public void delete(Long meetingId) {
        meetingRepository.delete(meetingId);
    }

    public Meeting retrieve(Long meetingId) {
        return meetingRepository.findOne(meetingId);
    }

    public Meeting retrieveByMeetingId(String meetingId) {
        return meetingRepository.findByMeetingId(meetingId).get(0);
    }

    public List<Meeting> retrieveAllByStatus(String status) {
        return meetingRepository.findByStatus(status);
    }
}
