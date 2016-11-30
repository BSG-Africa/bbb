package za.co.bsg.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.model.User;


import static org.hibernate.criterion.Restrictions.eq;

@Repository
@Transactional
public class UserRepository {
    private SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User getUserByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(eq("username", username));
        return (User) criteria.uniqueResult();
    }

    // this is for testing and needs to be removed at a later stage
    public User saveUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        return (User) session.merge(user);
    }
}
