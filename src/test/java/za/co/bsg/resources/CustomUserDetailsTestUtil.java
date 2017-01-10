package za.co.bsg.resources;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import za.co.bsg.model.User;

public class CustomUserDetailsTestUtil implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


    public UserDetails loadUserByUsername(User user) throws UsernameNotFoundException {
        return user;
    }
}
