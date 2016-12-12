package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.model.Meeting;
import za.co.bsg.repository.MeetingRepository;

import java.util.List;

@Service
public class MeetingDataService {

    @Autowired
    MeetingRepository meetingRepository;

    public List<Meeting> retrieveAllByUserId(long userId){
        return meetingRepository.findByCreatedBy(userId);
    }

    public Meeting save(Meeting meeting) {
        return meetingRepository.save(meeting);
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
}
