package br.com.ecommerce.common.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of the {@link UserDetails} interface used to represent and reference a user 
 * in the Spring Security context.
 * <p>
 * This class is typically created using data extracted from a JWT token. It allows Spring 
 * Security to handle authentication and authorization by providing user details, such as 
 * authorities (roles), to secure routes. Additionally, the user’s primary key (ID) can be 
 * easily retrieved, enabling endpoints to reference the user when needed.
 * </p>
 * <p>
 * The {@code UserDetailsImpl} stores the user's ID, username, and role, which are used to 
 * manage security operations and grant access to certain resources based on the user's role.
 * </p>
 */
public class UserDetailsImpl implements UserDetails {

    private String id;
    private String username;
    private String role;

    public UserDetailsImpl(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl(" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ')';
    }
}