package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.security.MyUserDetails;
import com.openclassrooms.paymybuddy.security.MyUserDetailsService;
import com.openclassrooms.paymybuddy.security.SpringSecurityConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
public class SecurityUtilities {
    /**
     * The Current User Connected
     */
    public final static String currentUser = "dada@email.fr";





}
