package com.openclassrooms.paymybuddy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
public class SecurityUtilities {
    /**
     * The Current User Connected
     */
    public final static String currentUser = getCurrentUser();

    /**
     * Method that get the current user in the SecurityContextHolder
     *
     * @return A String containing user's current user
     */
    @PreAuthorize("isAuthenticated()")
    public final static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userAuthenticated = (UserDetails) auth.getPrincipal();

        return userAuthenticated.getUsername();
    }



}
