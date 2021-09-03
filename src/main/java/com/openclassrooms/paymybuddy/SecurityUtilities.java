package com.openclassrooms.paymybuddy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@Slf4j
public class SecurityUtilities {


    /**
     * The Current User Connected
     */
//     public static String currentUser = getCurrentUser();


    /**
     * Method that get the current user in the SecurityContextHolder
     *
     * @return A String containing user's current user
     */
//    @PreAuthorize("isAuthenticated()")
    public static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
            UserDetails userAuthenticated = (UserDetails)auth.getPrincipal();
            log.debug("SecurityUtilities: User authenticated: " + userAuthenticated.getUsername());
            return userAuthenticated.getUsername();
//        }
//        return null;
    }

//    public static void setCurrentUser(String currentUser) {
//        SecurityUtilities.currentUser = getCurrentUser();
//    }


}
