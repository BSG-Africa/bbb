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
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.util.UtilServiceImp;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.util.Properties;
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
    UserRepository userRepository;

    @Autowired
    UtilServiceImp utilServiceImp;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findUserByUsername(username);
        return appUser;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User details = new User();
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        DirContext context = null;
        try {
            if (utilServiceImp.usernameContainsCompanyEmail(username)) {
                String sidUsername = utilServiceImp.getUsernameFromEmail(username);
                context = this.getDirContext(principalPrefix + sidUsername, password);
                if (context != null) {
                    System.out.println("Successfully logged  " + username + " on " + ldapUrl);
                    if (userRepository.findUserByUsername(username) == null) {
                        this.createUser(context, sidUsername, username, password);
                    }
                    details = userRepository.findUserByUsername(username);
                    System.out.println("Loaded " + details.getName());
                }
            } else {
                details = this.additionalAuthentication(username, password);
            }
            return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
        } catch (NamingException ex) {
            System.out.println("Could not login into '" + ldapUrl + ": " + ex);
            throw new BadCredentialsException(ex.getMessage());
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ex) {
                    System.out.println("Could not close DirContext: " + ex);
                }
            }
        }
    }

    private void createUser(DirContext context, String sidUsername, String username, String password) {
        String passwordHashed = utilServiceImp.hashPassword(password);
        User details = this.loadUserByUsername(context, sidUsername, username, passwordHashed);
        System.out.println(details);
        userRepository.save(details);
    }

    private User loadUserByUsername(DirContext context, String sidUsername, String username, String password) throws UsernameNotFoundException {
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // search for username
            NamingEnumeration<SearchResult> renum = context.search(userSearchBase, "(&(objectClass=user)(sAMAccountName={0}))",
                    new Object[]{sidUsername}, controls);
            if (!renum.hasMoreElements()) {
                throw new UsernameNotFoundException("User '" + sidUsername + "' is not exist");
            }
            SearchResult result = renum.next();
            final Attributes attributes = result.getAttributes();

            // User's display name
            String displayName = null;
            Attribute attr = attributes.get(displayNameAttribute);
            if (attr != null) {
                displayName = attr.get().toString();
            }
            if (!StringUtils.hasText(displayName)) displayName = sidUsername;

            // Is user blocked
            boolean blocked = false;
            attr = attributes.get("userAccountControl");
            if (attr != null) {
                blocked = (Long.parseLong(attr.get().toString()) & 2) != 0;
            }

            User appUser = getAppUser(username, password, displayName, blocked);
            return appUser;
        } catch (NamingException ex) {
            System.out.println("Could not find user '" + sidUsername + ": " + ex);
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    private User getAppUser(String username, String password, String displayName, boolean blocked) {
        User appUser = new User();
        appUser.setName(displayName);
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(UserRoleEnum.ADMIN.toString());
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
            System.out.println("Authentication failed for " + username + ": " + e);
            return null;
        }

        return context;
    }

    private User additionalAuthentication(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        if (password == null || password.length() == 0) {
            return null;
        }

        String passwordHash = utilServiceImp.hashPassword(password);
        if (!passwordHash.equals(user.getPassword())) {
            return null;
        }
        return user;
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
