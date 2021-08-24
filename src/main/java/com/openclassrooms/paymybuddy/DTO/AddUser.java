package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddUser {
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
    @NotBlank(message = "Email cannot be blank")
    private String email;

    /**
     * A String containing the confirm email
     */
    @NotBlank(message = "Confirm email cannot be blank")
    private String confirmEmail;

    /**
     * A Integer containing the account bank
     */
    @NotNull(message = "Account Bank cannot be null")
    private Integer accountBank;



    @Override
    public String toString() {
        return "UpdateProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + email + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
