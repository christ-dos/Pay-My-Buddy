package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendList {

    @NotBlank(message = "Email cannot be empty")
    private String email;

    private String firstName;

    private String lastName;


    @Override
    public String toString() {
        return "FriendList{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
