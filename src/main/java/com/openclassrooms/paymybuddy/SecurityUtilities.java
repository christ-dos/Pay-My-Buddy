package com.openclassrooms.paymybuddy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class SecurityUtilities {
    /**
     * The Current User Connected
     */
    public final static String userEmail = "dada@email.fr";
}
