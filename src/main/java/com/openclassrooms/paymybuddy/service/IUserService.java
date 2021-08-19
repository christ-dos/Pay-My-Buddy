package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateProfile;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;

import java.util.List;

public interface IUserService {
    Iterable<User> getUsers();

    User getUserByEmail(String email);

    Friend addFriendCurrentUserList(String friendEmail);

    List<FriendList> getFriendListByCurrentUserEmail();

    User addUser(UpdateProfile updateProfile);
}
