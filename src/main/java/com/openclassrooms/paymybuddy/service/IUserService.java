package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.User;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;

public interface IUserService {
    Iterable<User> getUsers();

    void addFriendUser(String userEmail, String FriendEmail) throws SQLIntegrityConstraintViolationException;


    Set<IFriendList> getFriendListByEmail(String userEmail);
}
