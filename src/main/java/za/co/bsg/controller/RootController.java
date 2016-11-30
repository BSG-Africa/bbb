package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import za.co.bsg.repository.UserRepository;

@Controller
public class RootController {
    private UserRepository userRoleDetailsDAO;

    @Autowired
    public RootController(UserRepository userRoleDetailsDAO) {
        this.userRoleDetailsDAO = userRoleDetailsDAO;
    }

//    @RequestMapping("/")
//    public String home() {
//        System.out.print("Hello");
////        this.PersistStuff(); // to test persist
//        return "login";
//    }
//
////    @RequestMapping(value = "/getUserDetails/", method = RequestMethod.POST)
////    @ResponseBody
////    public void postGetUsers(@RequestParam String userCred, @RequestParam String pass){
////        String name = userCred;
////        String passs = pass;
//////        String passwordForUSer = password;
////
////    }
//
//    @RequestMapping(value = "getUserDetails", method = RequestMethod.POST)
//    public String validatePageSubmit(@ModelAttribute(value = "user") User user, Model model) {
//        String username = user.getUsername();
//
//        return "index";
//    }
//
//    @RequestMapping(value = "/getUserDetail/{getUser}", method = RequestMethod.GET)
//    @ResponseBody
//    public void getUsers(@PathVariable String object) {
//        String name = object;
////        String passwordForUSer = password;
//
//    }
////    private void PersistStuff() {
////        UserRole userRole = new UserRole("Moderator");
////        User user = new User("kapeshi.kongolo", "Welcome", "Kapeshi", "Kongolo", userRole);
////        userRoleDetailsDAO.save(userRole);
////        userRoleDetailsDAO.saveUser(user);
////    }

}
