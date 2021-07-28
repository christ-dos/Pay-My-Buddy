package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Class Service that manage User entity
 *
 * @author Christine Duarte
 */
@Service
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {

        this.userRepository = userRepository;
    }

    /**
     * Method that get list of users
     *
     * @return An Iterable of User
     */
    @Override
    public Iterable<User> getUsers() {
        log.info("UserService: Display list of Users");
        return userRepository.findAll();
    }

    /**
     * Method which add a User friend to the table friend
     *
     * @param friendEmail email of the friend that will be added to the list
     * @param userEmail   Email of the user which want added a friend to the list
     * @throws UserNotFoundException if the user is not found in the database
     */
    @Override
    public void addFriendUser(String userEmail, String friendEmail) {
//        User friendToAdded = getUserByEmail(friendEmail);
////        if (friendToAdded == null) {
////            log.error("UserService: User not found with email: " + friendEmail);
////            throw new UserNotFoundException("User not found, please enter a email valid");
////        }
        log.debug("Service: User added with email: " + friendEmail);
        userRepository.saveFriend(userEmail, friendEmail);
    }

    /**
     * Method which get a user by email
     *
     * @param email item unique that permit identify the user
     * @return A user
     * @throws UserNotFoundException if the user is not found in the database
     */
    public User getUserByEmail(String email) {
        log.error("UserService: User found with email: " + email);
        return userRepository.findByEmail(email);
    }

    /**
     * Method which get list of friend recorded by a user
     *
     * @param userEmail A String containing the email of the user which want added a friend in his list
     * @return A set of {@link IFriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    @Override
    public Set<IFriendList> getFriendListByEmail(String userEmail) {

        return userRepository.findFriendListByEmail(userEmail);
    }

}
