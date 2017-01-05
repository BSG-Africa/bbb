package za.co.bsg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppPropertiesConfiguration {

    @Value("${external.authentication.ldap.url}")
    private String ldapUrl;

    @Value("${external.authentication.ldap.domain}")
    private String ldapDomain;

    @Value("${external.search.base}")
    private String ldapSearchBase;

    @Value("${bbb.server.url}")
    private String bbbURL;

    @Value("${bbb.server.salt}")
    private String bbbSalt;

    @Value("${display.name.attribute}")
    private String displayNameAttribute;

    @Value("${bbb.public.attendee}")
    private String attendeePW;

    @Value("${bbb.public.moderator}")
    private String moderatorPW;

    @Value("${bbb.logout.url}")
    private String logoutURL;

    @Value("${bbb.upload.path}")
    private String uploadPath;

    @Value("${bbb.upload.url}")
    private String uploadURL;

    public String getLdapUrl() {
        return ldapUrl;
    }

    public String getLdapDomain() {
        return ldapDomain;
    }

    public String getBbbURL() {
        return bbbURL;
    }

    public String getBbbSalt() {
        return bbbSalt;
    }

    public String getLdapSearchBase() {
        return ldapSearchBase;
    }

    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    public String getAttendeePW() {
        return attendeePW;
    }

    public String getModeratorPW() {
        return moderatorPW;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public String getUploadURL() {
        return uploadURL;
    }
}
