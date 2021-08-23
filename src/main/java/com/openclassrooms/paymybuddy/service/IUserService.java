package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateProfile;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    Iterable<User> getUsers();

    Friend addFriendCurrentUserList(String friendEmail);

    User getUserByEmail(String email);

    User addUser(UpdateProfile updateProfile);

    Page<FriendList> getFriendListByCurrentUserEmailPaged(Pageable pageable);

    List<FriendList> getFriendListByCurrentUserEmail();
}
