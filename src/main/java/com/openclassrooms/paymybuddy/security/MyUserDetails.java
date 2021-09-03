package com.openclassrooms.paymybuddy.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Class that Provides core user information.
 * <p>
 * * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
public class MyUserDetails implements UserDetails {
    /**
     * A string containing the username of the user
     */
    private String username;

    /**
     * A string containing the password of the user
     */
    private String password;

    /**
     * getter to obtain the authorities of the user
     *
     * @return A Collection containing the roles authorised for the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
//                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * getter to obtain the password of the user
     *
     * @return A String containing the password for the user
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * getter to obtain the username of the user
     *
     * @return A String containing the username for the user
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * getter to know if the account is expired or not
     *
     * @return A boolean, is true if the account is not expired else false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * getter to know if the account is locked or not
     *
     * @return A boolean, is true if the account is not locked else false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * getter indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return A boolean, is true if credentials are not expired else false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * getter to indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return A boolean, is true if user is enabled  else false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Method toString
     *
     * @return a String of the object MyUserDetails
     */
    @Override
    public String toString() {
        return "MyUserDetails{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
