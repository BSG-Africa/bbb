package za.co.bsg.dataAccess;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.models.User;
import za.co.bsg.models.UserRole;

import java.util.List;

@Repository
@Transactional
public class UserRoleDetailsDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public UserRoleDetailsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UserRole save(UserRole userRole) {
        Session session = sessionFactory.getCurrentSession();
        return (UserRole) session.merge(userRole);
    }

    public List<UserRole> retrieveUseRoleList() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(UserRole.class);
        return (List<UserRole>) criteria.list();
    }

    // this is for testing and needs to be removed at a later stage
    public User saveUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        return (User) session.merge(user);
    }
}
