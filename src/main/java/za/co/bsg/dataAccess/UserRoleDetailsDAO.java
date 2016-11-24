package za.co.bsg.dataAccess;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.bsg.models.UserRole;

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
}
