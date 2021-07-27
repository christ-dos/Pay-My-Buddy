package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendList implements IFriendList {

    @NotEmpty(message= "Friend email cannot be empty")
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

}
