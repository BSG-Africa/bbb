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

    public List<Meeting> findByCreatedBy(String createdBy){
        return meetingRepository.findByCreatedBy(createdBy);
    }

    public List<Meeting> findAll(){
        return meetingRepository.findAll();
    }

    public Meeting Save(Meeting meeting){
        return (Meeting) meetingRepository.save(meeting);
    }
}
