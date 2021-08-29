package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.security.MyUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Getter
@Setter
public class SecurityUtilities {

    public final static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userAuthenticated = (MyUserDetails) auth.getPrincipal();

        return userAuthenticated.getUsername();
    }

    /**
     * The Current User Connected
     */
    public final static String currentUser = getCurrentUser();

    public SecurityUtilities() {
    }
}
