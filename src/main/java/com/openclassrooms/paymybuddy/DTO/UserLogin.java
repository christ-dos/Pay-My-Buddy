package com.openclassrooms.paymybuddy.DTO;

import com.openclassrooms.paymybuddy.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {

    private User user;

    public UserLogin(User user) {
        this.user = user;
    }

    /**
     * Attribute containing a string with the email
     */
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * Attribute containing a string with the password
     */
    private String password;

}
