package za.co.bsg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.bsg.dataAccess.UserRoleDetailsDAO;
import za.co.bsg.models.UserRole;

@Controller
public class RootController {
    private UserRoleDetailsDAO userRoleDetailsDAO;

    @Autowired
    public RootController(UserRoleDetailsDAO userRoleDetailsDAO) {
        this.userRoleDetailsDAO = userRoleDetailsDAO;
    }

    @RequestMapping("/")
    public String home() {
        System.out.print("Hello");
        this.PersistStuff();

        // fix database interaction
        return "index";
    }

    private void PersistStuff() {
        UserRole userRole = new UserRole("Moderator");
        userRoleDetailsDAO.save(userRole);
    }

}
