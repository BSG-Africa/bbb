package za.co.bsg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.util.UtilService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UtilService utilService;

    /**
     * This method finds all users in the User table
     *
     * @return list of User objects
    */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> users() {
        return userRepository.findAll();
    }

    /**
     * This method finds a user in the user table by user id
     * if a user does not exist a ResponseEntity with a NO_CONTENT HttpStatus is returned
     * if a user exists a ResponseEntity containing a User object and OK HttpStatus is returned
     *
     * @param id a Long data type - which is the id used to find user by
     * @return ResponseEntity<User> containing a HttpStatus and/or  User object
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> userById(@PathVariable Long id) {
        User appUser = userRepository.findOne(id);
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }
    }

    /**
     * This method deletes a User object from the user table by
     * getting the object to be deleted from the user table and authentication
     * if a user does not exist a ResponseEntity with NO_CONTENT HttpStatus is returned
     * if a user exists and user to be deleted is a current logged on user,
     * a RuntimeException is thrown indicating user can't delete own account
     * if a user exist and their role is an Admin role a RuntimeException is thrown
     * indicating user can't delete an admin user
     * if a user exists and is not the current logged on user or an admin role,
     * then the user is deleted.
     *
     * @param id a Long data type - which is used to delete user by
     * @return ResponseEntity<User> containing a User object and HttpStatus
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        User appUser = userRepository.findOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        if (appUser == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } else if (appUser.getUsername().equalsIgnoreCase(loggedUsername)) {
            throw new RuntimeException("You cannot delete your account");
        } else if(appUser.getRole().equalsIgnoreCase(UserRoleEnum.ADMIN.toString())){
            throw new RuntimeException("You cannot delete an admin account");
        }
        else {
            try {
                userRepository.delete(appUser);
            }
            catch (RuntimeException e){
                throw new RuntimeException("Delete unsuccessful!");
            }
            return new ResponseEntity<User>(appUser, HttpStatus.OK);
        }

    }

    /**
     * This method creates a user in the user table by checking if username already exists,
     * if a user already exists a RuntimeException is thrown indicating that user already exists
     * if a user does not exist a hash password is set and user to user table.
     *
     * @param appUser a user data type - a user object used to create a user
     * @return n ResponseEntity<User> containing a User object and HttpStatus
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null) {
            throw new RuntimeException("Username already exist");
        }
        String passwordHashed = utilService.hashPassword(appUser.getPassword());
        appUser.setPassword(passwordHashed);
        return new ResponseEntity<User>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    /**
     *  This method edits a user in the user table using User object parsed as parameter.
     *  If a user exists and the user id of the existing user and edit user objects are different,
     *  then a RuntimeException is
     *  if user exists and user id of both the existing user and edit object user are the same,
     *  then the existing user is updated.
     *
     * @param appUser a User data type -  user object used to edit a user
     * @return a User object
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public User editUser(@RequestBody User appUser) {
        if (userRepository.findUserByUsername(appUser.getUsername()) != null
                && userRepository.findUserByUsername(appUser.getUsername()).getId() != appUser.getId()) {
            throw new RuntimeException("Username already exist");
        }
        else if (userRepository.findOne(appUser.getId()) != null
                && userRepository.findOne(appUser.getId()).getId() == appUser.getId()) {
            appUser.setPassword(userRepository.findOne(appUser.getId()).getPassword());
        }
        return userRepository.save(appUser);
    }

}
