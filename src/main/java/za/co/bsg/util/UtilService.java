package za.co.bsg.util;


public interface UtilService {

    String hashPassword(String password);

    String getUsernameFromEmail(String email);

    boolean usernameContainsCompanyEmail(String username);

}