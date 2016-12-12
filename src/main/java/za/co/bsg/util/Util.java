package za.co.bsg.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.util.UUID;

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

    public static String generateMeetingId() {
        StringBuilder s = new StringBuilder();
        String letters = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
        String numbers = "0123456789";
        String randoms = UUID.randomUUID().toString();
        String all = letters + numbers + randoms;
        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasRandom = false;
        for (int i = 0; i < 16; i++) {
            char c = all.charAt((int) (Math.random() * all.length()));
            if (!hasLetter && (letters.contains("" + c))) {
                hasLetter = true;
            } else if (!hasNumber && numbers.contains("" + c)) {
                hasNumber = true;
            } else if (!hasRandom && randoms.contains("" + c)) {
                hasRandom = true;
            }
            s.append(c);
        }
        if (!hasLetter) {
            s.append(letters.charAt((int) (Math.random() * letters.length())));
        }
        if (!hasNumber) {
            s.append(numbers.charAt((int) (Math.random() * numbers.length())));
        }
        if(!hasRandom) {
            s.append(randoms.charAt((int) (Math.random() * randoms.length())));
        }
        return s.toString();
    }
}
