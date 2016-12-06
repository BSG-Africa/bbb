package za.co.bsg.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.bsg.model.Meeting;

import java.util.ArrayList;
import java.util.List;

public class MeetingRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public MeetingRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public List<Meeting> findAllMeetings(){
        return new ArrayList<>();
    }

    public List<Meeting> findAllMeetingsByModeratorId(){
        return new ArrayList<>();
    }

    public List<Meeting> createMeeting(){
        return new ArrayList<>();
    }
}
