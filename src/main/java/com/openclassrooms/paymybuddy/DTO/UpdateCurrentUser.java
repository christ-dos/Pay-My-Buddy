package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateCurrentUser {

    /**
     * Attribute that containing the first name of current user
     */
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    /**
     * Attribute that containing the last name of current user
     */
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    /**
     * A String containing the password
     */
    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;

    /**
     * A String containing the confirm password
     */
    @NotBlank(message = "Confirm password cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String confirmPassword;

    /**
     * A String containing the email
     */
    private String email;

    @Override
    public String toString() {
        return "UpdateCurrentUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
