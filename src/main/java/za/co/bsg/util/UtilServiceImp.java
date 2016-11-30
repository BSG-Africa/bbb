package za.co.bsg.util;

import org.springframework.stereotype.Service;

@Service
public class UtilServiceImp implements UtilService {

    @Override
    public String hashPassword(String password) {
        return Util.hashPassword(password);
    }

    @Override
    public String getUsernameFromEmail(String email) {
        return Util.extractUsername(email);
    }

    @Override
    public boolean usernameContainsCompanyEmail(String username) {
        return Util.usernameContainsEmail(username);
    }
}
