package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;

public interface IUserService {
    public Iterable<User> getUsers();
    public void addFriendUser(String userEmail, String FriendEmail);
}
