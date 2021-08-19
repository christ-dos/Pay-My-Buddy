package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class Service that manage User entity
 *
 * @author Christine Duarte
 */
@Service
@Slf4j

public class UserService implements IUserService {
    /**
     * An instance of {@link IUserRepository}
     */
    private final IUserRepository userRepository;

    private final IFriendRepository friendRepository;

    /**
     * Constructor of the class
     *
     * @param userRepository   An instance of {@link IUserRepository}
     * @param friendRepository An instance of {@link IFriendRepository}
     */
    @Autowired
    public UserService(IUserRepository userRepository, IFriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    /**
     * Method that get list of all users
     *
     * @return An Iterable of User
     */
    @Override
    public Iterable<User> getUsers() {
        log.info("UserService: Display list of Users");
        return userRepository.findAll();
    }

    /**
     * Method which add a User to the table User
     *
     * @return A {@link User} Object
     */
    @Override
    public User addUser(User user) {
        User userToUpdate = userRepository.findByEmail(SecurityUtilities.userEmail);
        userToUpdate.setEmail(SecurityUtilities.userEmail);
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setPassword(user.getPassword());
        return userRepository.save(userToUpdate);
    }

    /**
     * Method which add a User friend to the table friend
     *
     * @param friendEmail email of the friend that will be added to the list
     * @return A user Object
     */
    @Override
    public Friend addFriendCurrentUserList(String friendEmail) {
        Friend friendToAdd = new Friend(SecurityUtilities.userEmail, friendEmail, LocalDateTime.now());
        if (!userEmailIsPresentDataBase(friendEmail)) {
            log.error("Service: User's email not Exist in data base");
            throw new UserNotFoundException("User's email not exist");
        }
        if (friendAlreadyExistsInList(friendEmail)) {
            log.error("Service: user's email already exists in friend list");
            throw new UserAlreadyExistException("user already exist in list of connections");
        }
        log.debug("Service: friend User added with email: " + friendEmail);

        return friendRepository.save(friendToAdd);
    }

    /**
     * Method which get a user by email
     *
     * @param email item unique that permit identifies the user
     * @return A User
     */
    public User getUserByEmail(String email) {
        log.debug("UserService: User found with email: " + email);

        return userRepository.findByEmail(email);
    }

    /**
     * Method which get list of friend recorded by a user
     *
     * @return A list of {@link FriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    @Override
    public List<FriendList> getFriendListByCurrentUserEmail() {
        List<Friend> friendListsByEmail = friendRepository.findByUserEmailOrderByDateAddedDesc(SecurityUtilities.userEmail);
        log.debug("UserService: friend list found for current user: " + SecurityUtilities.userEmail);

        return friendListsByEmail.stream().map(friend -> {
            User user = userRepository.findByEmail(friend.getFriendEmail());
            return new FriendList(user.getEmail(), user.getFirstName(), user.getLastName());
        }).collect(Collectors.toList());
    }

    /**
     * Private method that verify if the friend already exist in the list
     *
     * @param friendEmail A string containing the email of the friend
     * @return true if the friend already exist in list else return false
     */
    private Boolean friendAlreadyExistsInList(String friendEmail) {
        List<Friend> listFriend = friendRepository.findByUserEmailOrderByDateAddedDesc(SecurityUtilities.userEmail);
        for (Friend friend : listFriend) {
            if (friend.getFriendEmail().equals(friendEmail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private method that check if the friend that we want added exist in database
     *
     * @param friendEmail A string containing the email of the friend that aw want added
     * @return True if the friend exist in database and false if not exist
     */
    private Boolean userEmailIsPresentDataBase(String friendEmail) {
        if (userRepository.findByEmail(friendEmail) == null) {
            return false;
        }
        return true;
    }
}
