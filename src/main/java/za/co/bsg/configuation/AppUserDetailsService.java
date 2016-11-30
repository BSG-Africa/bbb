package za.co.bsg.configuation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.bsg.dataAccess.AppUserRepository;
import za.co.bsg.models.User;

/**
 * This Service class for providing the user credentials from the database.
 *
 * @author Sarath Muraleedharan
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = appUserRepository.findOneByUsername(username);
        return appUser;
    }

}
