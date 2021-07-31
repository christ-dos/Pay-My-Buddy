package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendList implements IFriendList {

    @NotBlank(message = "Email cannot be empty")
    private String email;

    private String firstName;

    private String lastName;


    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }


    @Override
    public String toString() {
        return "FriendList{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
