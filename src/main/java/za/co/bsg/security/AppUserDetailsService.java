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
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.enums.UserRoleEnum;
import za.co.bsg.model.User;
import za.co.bsg.repository.UserRepository;
import za.co.bsg.util.UtilService;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.util.*;


@Service
public class AppUserDetailsService implements UserDetailsService, AuthenticationProvider {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    private AppPropertiesConfiguration appPropertiesConfiguration;

    /**
     * This method returns user details of the user logging on the system
     *
     * @param username a String data type - username of the user logging on
     * @return a UserDetails object
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    /**
     *  This method authenticate user by
     *
     * @return a Authentication object
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User details;
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        DirContext context = null;
        try {
            details = this.authenticateUser(username, password);
            if(details != null){
                //Logged in successfully on our local db
                return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
            } else if (utilService.usernameContainsCompanyEmail(username)) {
                String sidUsername = utilService.getUsernameFromEmail(username);
                context = this.getActiveDirectoryContext(getPrincipalPrefix() + sidUsername, password);
                if (context != null) {
                    details = userRepository.findUserByUsername(username);
                    if (details == null) {
                        //First time login
                        System.out.println("Successfully logged  " + username + " on " + getLdapUrl());
                        details = this.createUser(context, sidUsername, username, password);
                    } else{
                        //Password has changed, update in our db
                        details.setPassword(utilService.hashPassword(password));
                        details = userRepository.save(details);
                    }
                    return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
                }
            }
            return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
        } catch (NamingException ex) {
            System.out.println("Could not login into '" + getLdapUrl() + ": " + ex);
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

    /**
     *
     * @param context
     * @param sidUsername
     * @param username
     * @param password
     * @return
     */
    private User createUser(DirContext context, String sidUsername, String username, String password) {
        String passwordHashed = utilService.hashPassword(password);
        User details = this.loadUserByUsername(context, sidUsername, username, passwordHashed);
        addAdditionalRoles(context, details, sidUsername);
        return userRepository.save(details);
    }

    private void addAdditionalRoles(DirContext context, User appUser, String sidUsername){
        String superAdminGroup = getActiveDirectorySuperAdminGroup();
        Set<String> groups = getUserGroups(context, sidUsername);
        if(groups.contains(superAdminGroup)){
            appUser.getAdditionalRoles().add(UserRoleEnum.SUPER_ADMIN.toString());
        }
    }

    /**
     * This method loads
     * if user is not found a UsernameNotFoundException is thrown
     *
     *
     * @param context
     * @param sidUsername
     * @param username
     * @param password
     * @return a User object
     * @throws UsernameNotFoundException
     */
    private User loadUserByUsername(DirContext context, String sidUsername, String username, String password) throws UsernameNotFoundException {
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // search for username
            NamingEnumeration<SearchResult> renum = context.search(getSearchBase(), "(&(objectClass=user)(sAMAccountName={0}))",
                    new Object[]{sidUsername}, controls);
            if (!renum.hasMoreElements()) {
                throw new UsernameNotFoundException("User '" + sidUsername + "' not found");
            }
            SearchResult result = renum.next();
            final Attributes attributes = result.getAttributes();

            // User's display name
            String displayName = null;
            Attribute attr = attributes.get(getDisplayNameAttribute());
            if (attr != null) {
                displayName = attr.get().toString();
            }
            if (!StringUtils.hasText(displayName)) displayName = sidUsername;

            // Is user blocked
            boolean blocked;
            blocked = false;
            attr = attributes.get("userAccountControl");
            if (attr != null) {
                blocked = (Long.parseLong(attr.get().toString()) & 2) != 0;
            }

            return getAppUser(username, password, displayName, blocked);
        } catch (NamingException ex) {
            System.out.println("Could not find user '" + sidUsername + ": " + ex);
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    /**
     * This method returns the groups the user belongs to
     *
     * @param ctx the ldap context
     * @param username the user sid username
     * @return Set of user groups
     */
    private Set<String> getUserGroups(DirContext ctx, String username){
        final Set<String> groups = new HashSet();
        try {
            //Create the search controls
            SearchControls searchCtls = new SearchControls();

            //Specify the attributes to return
            String returnedAttributes[]={"memberOf"};
            searchCtls.setReturningAttributes(returnedAttributes);

            //Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);


            //specify the LDAP search filter
            String searchFilter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

            // Search for objects using the filter
            NamingEnumeration answer = ctx.search(getSearchBase(), searchFilter, searchCtls);


            //Loop through the search results
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult)answer.next();
                Attributes attrs = sr.getAttributes();
                Attribute groupMembers = attrs.get("memberOf");

                for (int i=0; i<groupMembers.size(); i++) {
                    Attribute attr = ctx.getAttributes(groupMembers.get(i).toString(), new String[]{"CN"}).get("CN");
                    if (attr != null) {
                        groups.add(attr.get().toString());
                    }
                }
            }
        }catch (NamingException e) {
            System.err.println("Failed to find user "+username + " Active Directory group: " + e);
        }

        return groups;
    }

    /**
     * This method creates a User object and sets user fields to parsed parameters
     *
     * @param username a String data type - used to set a user's username
     * @param password a String data type - user to set a user's password
     * @param displayName a String data type - used to set a user's Name
     * @param blocked a boolean data type - used to set a user's blocked status
     * @return a User object
     */
    private User getAppUser(String username, String password, String displayName, boolean blocked) {
        User appUser = new User();
        appUser.setName(displayName);
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setRole(UserRoleEnum.ADMIN.toString());
        appUser.setBlocked(blocked);
        return appUser;
    }

    /**
     * This method sets initial context using specified properties,
     * if credentials are invalid a NamingException is thrown indicating that authentication failed
     *
     * @param username a String data type - username used to specify name of user/program
     *                 doing the authentication
     * @param password a String data type - password used to specify credentials of user/program
     *                 doing authentication
     * @return a DirContext object
     * @throws NamingException
     */
    private DirContext getActiveDirectoryContext(String username, String password) throws NamingException {
        final Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.PROVIDER_URL, getLdapUrl());
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

    /**
     * This method finds a user by the username in the user table.
     * If the user exists, a comparison is done between the hashed
     * user password parsed as a parameter and the existing password
     * in the user table.  If the passwords match the User object
     * is returned otherwise a null value is returned.
     *
     * @param username a String data type - Which is the username
     *                 provided by a user for authentication
     * @param password a String data type - Which is the user password
     *                 provided by a user for authentication
     * @return a User object or null value
     */
    private User authenticateUser(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        String passwordHash = utilService.hashPassword(password == null ? "" : password);
        if (user != null && passwordHash.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    private static final Control[] CONTROLS = new Control[]{new FastBindConnectionControl()};

    /**
     * This supposedly provides faster auth against AD.
     * See: https://msdn.microsoft.com/en-us/library/aa366981(v=vs.85).aspx
     * See: http://stackoverflow.com/questions/11493742/fastbind-for-authentication-against-active-directory-using-spring-ldap
     * See: https://community.oracle.com/thread/1157584?tstart=0
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

    private String getLdapUrl() {
        return appPropertiesConfiguration.getLdapUrl();
    }

    private String getDisplayNameAttribute() {
        return appPropertiesConfiguration.getDisplayNameAttribute();
    }

    private String getPrincipalPrefix() {
        return appPropertiesConfiguration.getLdapDomain();
    }

    private String getSearchBase() {
        return appPropertiesConfiguration.getLdapSearchBase();
    }

    private String getActiveDirectorySuperAdminGroup() {
        return appPropertiesConfiguration.getSuperAdminGroup();
    }
}
