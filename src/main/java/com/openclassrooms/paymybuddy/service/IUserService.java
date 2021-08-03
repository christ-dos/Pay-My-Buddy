package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;

import java.util.List;

public interface IUserService {
    Iterable<User> getUsers();

    User getUserByEmail(String email);

    Friend addFriendUser(String userEmail, String FriendEmail);

    List<FriendList> getFriendListByEmail(String userEmail);

}
