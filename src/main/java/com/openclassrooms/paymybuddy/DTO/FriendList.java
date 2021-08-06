package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/** Class DTO that permit displaying in view the email, firstName and Lastname
 * in list nof connections
 *
 * @author Christine Duarte
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendList {
    /**
     * Attribute containing a string with the email
     */
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * Attribute containing a string with the firstName
     */
    private String firstName;

    /**
     * Attribute containing a string with the lastName
     */
    private String lastName;

    /**
     * Method ToString
     * @return A string of the object FriendList
     */
    @Override
    public String toString() {
        return "FriendList{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
