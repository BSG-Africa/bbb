package za.co.bsg.dataAccess;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.model.Meeting;

@Repository
@Transactional
public class MeetingDAO {

    private SessionFactory sessionFactory;

    @Autowired
    public MeetingDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public Meeting Save(Meeting meeting){
        Session session = sessionFactory.getCurrentSession();
        return (Meeting) session.merge(meeting);
    }
}
