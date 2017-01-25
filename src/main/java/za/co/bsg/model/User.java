package za.co.bsg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Column(unique = true)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column
    private String password;
    @Column
    private String role;
    @Column
    private boolean blocked;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "moderator", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Meeting> moderators = new HashSet();

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "createdBy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Meeting> creators = new HashSet();

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "modifiedBy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Meeting> modifiers = new HashSet();

    @Column
    @ElementCollection(fetch=FetchType.EAGER, targetClass=String.class)
    private Set<String> additionalRoles = new HashSet();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonIgnore
    public Set<String> getAdditionalRoles() {
        return additionalRoles;
    }

    @JsonIgnore
    public void setAdditionalRoles(Set<String> additionalRoles) {
        this.additionalRoles = additionalRoles;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @JsonIgnore
    public Set<Meeting> getModerators() {
        return moderators;
    }

    @JsonIgnore
    public Set<Meeting> getCreators() {
        return creators;
    }

    @JsonIgnore
    public Set<Meeting> getModifiers() {
        return modifiers;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority(role));
        for (String additionalRole : additionalRoles) {
            authorities.add(new SimpleGrantedAuthority(additionalRole));
        }
        return authorities;
    }
}
