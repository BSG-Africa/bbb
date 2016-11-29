package za.co.bsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.dataAccess.UserRoleDetailsDAO;
import za.co.bsg.enums.UserRolesSeed;
import za.co.bsg.models.UserRole;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SeedingService {

    private UserRoleDetailsDAO userRoleDetailsDAO;

    @Autowired
    public SeedingService(UserRoleDetailsDAO userRoleDetailsDAO) {
        this.userRoleDetailsDAO = userRoleDetailsDAO;
    }

    @PostConstruct
    public void SeedDataIfNotExists() {
        UserRolesSeed[] userRoles = UserRolesSeed.values();
        List<UserRole> userRolesdb = userRoleDetailsDAO.retrieveUseRoleList();

        OUTER_LOOP:
        for (UserRolesSeed userSeed : userRoles) {
            UserRole userRole = new UserRole(userSeed.getId(), userSeed.toString());
            for (UserRole userdb : userRolesdb) {
                if (userdb.getRole_id() == userSeed.getId()) {
                    continue OUTER_LOOP;
                }
            }
            userRoleDetailsDAO.save(userRole);
        }
    }
}
