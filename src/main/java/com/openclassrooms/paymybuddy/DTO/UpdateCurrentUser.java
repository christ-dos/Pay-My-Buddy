package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Class DTO {@link UpdateCurrentUser} that obtains input from the view profile to update information
 * of the current user
 *
 * @author Christine Duarte
 */
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
     * A String containing the email that we will update
     */
    private String email;

    /**
     * Method ToString
     *
     * @return A string of the object UpdateCurrentUser
     */
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
