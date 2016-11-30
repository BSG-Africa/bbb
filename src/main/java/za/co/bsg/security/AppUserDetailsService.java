package za.co.bsg.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.model.User;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class AppUserDetailsService implements UserDetailsService, AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(AppUserDetailsService.class.getName());
    private String ldapUrl = "ldap://ldap.bsg.co.za:389";
    private String displayNameAttribute = "displayName";
    private String emailAttribute = "mail";
    private String phoneAttribute = "telephoneNumber";
    private String userSearchBase = "DC=bsg,DC=local";
    private String principalPrefix = "BSG\\";

    @Autowired
    UserRepository userRoleDetailsDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRoleDetailsDAO.getUserByUsername(username);
        return appUser;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        logger.log(Level.FINE, "Performing logon into '" + ldapUrl + "' with credentials '" + username + "'/'" + password.replaceAll(".", "*") + "'");

        DirContext context = null;
        try {
            context = getDirContext(principalPrefix + username, password);
            logger.log(Level.FINE, "User '" + username + "' has been successfully logged on");
            User details;
            if(context!=null){
                System.out.println("Successfully logged on " + ldapUrl);
                if (userRoleDetailsDAO.getUserByUsername(username) == null) {
                    System.out.println("CREATING " + username);
                    details = loadUserByUsername(context, username, password);
                    userRoleDetailsDAO.saveUser(details);
                }
                details = userRoleDetailsDAO.getUserByUsername(username);
                System.out.println("Found " + details.getName());
            } else {
                System.out.println("Getting local user "+username);
                details = userRoleDetailsDAO.getUserByUsername(username);
            }
            return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Could not login into '" + ldapUrl + "'", ex);
            throw new BadCredentialsException(ex.getMessage());
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ex) {
                    logger.log(Level.WARNING, "Could not close DirContext", ex);
                }
            }
        }
    }

    private User loadUserByUsername(DirContext context, String username, String password) throws UsernameNotFoundException {
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // search for username
            NamingEnumeration<SearchResult> renum = context.search(userSearchBase, "(&(objectClass=user)(sAMAccountName={0}))",
                    new Object[]{username}, controls);
            if (!renum.hasMoreElements()) {
                throw new UsernameNotFoundException("User '" + username + "' is not exist");
            }
            SearchResult result = renum.next();
            final Attributes attributes = result.getAttributes();

            // User's display name
            String displayName = null;
            Attribute attr = attributes.get(displayNameAttribute);
            if (attr != null) {
                displayName = attr.get().toString();
            }
            if (!StringUtils.hasText(displayName)) displayName = username;
            logger.log(Level.FINE, "Display name: " + displayName);

            // User's email
            String email = null;
            attr = attributes.get(emailAttribute);
            if (attr != null) {
                email = attr.get().toString();
            }
            logger.log(Level.FINE, "E-mail: " + email);

            // Is user blocked
            boolean blocked = false;
            attr = attributes.get("userAccountControl");
            if (attr != null) {
                blocked = (Long.parseLong(attr.get().toString()) & 2) != 0;
            }
            logger.log(Level.FINE, "Blocked: " + blocked);

            User appUser = getAppUser(username, password, displayName, email, blocked);
            return appUser;
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Could not find user '" + username + "'", ex);
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    private User getAppUser(String username, String password, String displayName, String email, boolean blocked) {
        User appUser = new User();
        appUser.setName(displayName);
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(UserRoleEnum.ADMIN.toString());
        appUser.setEmail(email);
        appUser.setBlocked(blocked);
        return appUser;
    }

    private DirContext getDirContext(String username, String password) throws NamingException {
        final Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.PROVIDER_URL, ldapUrl);
        props.put(Context.SECURITY_PRINCIPAL, username);
        props.put(Context.SECURITY_CREDENTIALS, password);

        props.put("java.naming.ldap.attributes.binary", "objectSID");

        DirContext context;
        try {
            context = new InitialLdapContext(props, CONTROLS);
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "Authentication failed for " + username + ": " + e, e);
            return null;
        }

        return context;
    }

    private static final Control[] CONTROLS = new Control[]{new FastBindConnectionControl()};

    /**
     * This supposedly provides faster auth against AD.
     * See: http://forums.sun.com/thread.jspa?threadID=726601
     */
    private static class FastBindConnectionControl implements Control {
        private static final long serialVersionUID = -606709308560478694L;

        @Override
        public byte[] getEncodedValue() {
            return null;
        }
        @Override
        public String getID() {
            return "1.2.840.113556.1.4.1781";
        }
        @Override
        public boolean isCritical() {
            return true;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    public void setDisplayNameAttribute(String displayNameAttribute) {
        this.displayNameAttribute = displayNameAttribute;
    }

    public String getEmailAttribute() {
        return emailAttribute;
    }

    public void setEmailAttribute(String emailAttribute) {
        this.emailAttribute = emailAttribute;
    }

    public String getPhoneAttribute() {
        return phoneAttribute;
    }

    public void setPhoneAttribute(String phoneAttribute) {
        this.phoneAttribute = phoneAttribute;
    }
}
