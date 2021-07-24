package com.openclassrooms.paymybuddy.DTO;

import lombok.Setter;

@Setter
public class FriendList implements IFriendList {

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
    public String getlastName() {
        return lastName;
    }
}
