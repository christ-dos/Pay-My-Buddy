package com.openclassrooms.paymybuddy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class utils that manage the User logged
 */
@Getter
@Setter
@Slf4j
public class SecurityUtilities {
    /**
     * Method that get the current user in the SecurityContextHolder
     *
     * @return A String containing current user
     */
    public static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userAuthenticated = (UserDetails)auth.getPrincipal();
            log.debug("SecurityUtilities: User authenticated: " + userAuthenticated.getUsername());
            return userAuthenticated.getUsername();
    }
}
