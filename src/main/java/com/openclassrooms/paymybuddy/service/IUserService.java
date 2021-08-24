package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    Iterable<User> getUsers();

    User getUserByEmail(String email);

    Friend addFriendCurrentUserList(String friendEmail);

    User addUser(AddUser addUser);

    User updateProfile(UpdateCurrentUser updateCurrentUser);

    Page<FriendList> getFriendListByCurrentUserEmailPaged(Pageable pageable);

    List<FriendList> getFriendListByCurrentUserEmail();
}
