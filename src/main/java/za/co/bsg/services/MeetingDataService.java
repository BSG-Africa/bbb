package za.co.bsg.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.model.Meeting;
import za.co.bsg.repository.MeetingRepository;

@Service
public class MeetingDataService {

    @Autowired
    MeetingRepository meetingRepository;

    public Meeting Save(Meeting meeting){
        return (Meeting) meetingRepository.save(meeting);
    }
}
