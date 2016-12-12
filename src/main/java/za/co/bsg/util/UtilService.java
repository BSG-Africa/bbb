package za.co.bsg.util;


import org.springframework.stereotype.Service;

@Service
public interface UtilService {

    String hashPassword(String password);

    String getUsernameFromEmail(String email);

    boolean usernameContainsCompanyEmail(String username);

    String generateMeetingId();
}