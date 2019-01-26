package de.gruppe2.agamoTTTo.security;

import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the UserDetails interface of the Spring Framework.
 * The reason behind creating an own SecurityUser is the following:
 * We now get a real user object from the SecurityContext, which can be used for setting
 * the creator/editor of an entity without querying the database again.
 */
public class CustomSecurityUser extends User implements UserDetails {
    // Create a SecurityUser based on a user entity
    public CustomSecurityUser(User user) {
        super(user);
    }

    @Override
    public String getPassword() {
        return super.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return super.getEnabled();
    }

    // Override methods of UserDetails with our specific values
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(super.getRole().getRoleName());
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(authority);
        return grantedAuthorities;
    }

    // We need the following two methods, so that Spring Security can check, whether a user is logged in twice.
    @Override
    public int hashCode() {
        return getUsername() != null ? getUsername().hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return getUsername().equals(((CustomSecurityUser) obj).getUsername());
        }
        return false;
    }
}
