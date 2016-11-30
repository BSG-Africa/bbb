package za.co.bsg.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class Util {

    public static final String SALT = "oink42";
    public static final String EMAIL_DOMAIN = "@bsg.co.za";

    public static String hashPassword(String password) {
        Md5PasswordEncoder pe = new Md5PasswordEncoder();
        return pe.encodePassword(password, SALT);
    }

    public static String extractUsername(String email) {
        return email.split("@")[0];
    }

    public static boolean usernameContainsEmail(String username) {
        return username.toLowerCase().contains(EMAIL_DOMAIN);
    }
}
