package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.security.ISecurityContextFacade;
import com.openclassrooms.paymybuddy.security.MyUserDetails;
import com.openclassrooms.paymybuddy.security.MyUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class SecurityUtilities {
    @Autowired
    private static MyUserDetailsService myUserDetailsService;





    public static String getCurrentUser() {
      Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userAuthenticated = (MyUserDetails) auth.getPrincipal();

      return userAuthenticated.getUsername();
    }

    /**
     * The Current User Connected
     */
    public  static String currentUser = "dada@email.fr";


}
