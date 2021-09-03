package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.exception.EmailNotMatcherException;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    /**
     * Method that get list of all users
     *
     * @return An Iterable of User
     */
    Iterable<User> getUsers();

    /**
     * Method which get a user by email
     *
     * @param email item unique that permit identifies the user
     * @return A User
     */
    User getUserByEmail(String email);

    /**
     * Method which add a User friend to the table friend
     *
     * @param friendEmail email of the friend that will be added to the list
     * @return A user Object
     */
    Friend addFriendCurrentUserList(String friendEmail);

    /**
     * Method which add a user to the table User
     *
     * @param addUser A model DTO with user information
     * @return A {@link User} Object
     * @throws UserAlreadyExistException   when user email already exists in database
     * @throws EmailNotMatcherException    when email not match with confirm email
     * @throws PasswordNotMatcherException when password not match with confirm password
     */
    User addUser(AddUser addUser);

    /**
     * Method which update current user to the table User
     *
     * @param updateCurrentUser a model DTO to get the information from the view
     * @return A {@link User} Object updated
     * @throws PasswordNotMatcherException when password not match with confirm password
     */
    User updateProfile(UpdateCurrentUser updateCurrentUser);

    /**
     * Method which get list of friend recorded by a user with pagination
     *
     * @param pageable Abstract interface for pagination information.
     * @return A list of {@link FriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    Page<FriendList> getFriendListByCurrentUserEmailPaged(Pageable pageable);

    /**
     * Method which get list of friend recorded by a user
     *
     * @return A list of {@link FriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    List<FriendList> getFriendListByCurrentUserEmail();
}
