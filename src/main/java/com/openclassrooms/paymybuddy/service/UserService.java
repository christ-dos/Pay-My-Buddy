package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class Service that manage User entity
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
     * @return An Iterable of User
     */
    @Override
    public Iterable<User> getUsers(){
        log.info("UserService: Display list of Users");
        return userRepository.findAll();
    }

    /**
     * Method which add a User friend to the table friend
     * @param friendEmail  email of the friend that will be added to the list
     * @param userEmail Email of the user which want added a friend to the list
     *
     * @throws UserNotFoundException if the user is not found in the database
     */
    @Override
    public void addFriendUser(String userEmail, String friendEmail){
        Optional<User> userToAdded = getUserByEmail(friendEmail);
        if(!userToAdded.isPresent()){
            log.error("UserService: User not found with email: " + friendEmail);
            throw new UserNotFoundException("Service: User not found");
        }
        log.debug("Service: User added with email: " + friendEmail);
        userRepository.saveFriend(userEmail, friendEmail);
    }

    /**
     * Method which get a user by email
     *
     * @param email item unique that permit identify the user
     * @return An optinal set of User
     *
     * @throws UserNotFoundException if the user is not found in the database
     */
    private Optional<User> getUserByEmail(String email){
        if(email != null) {
            log.debug("UserService: user found with email: " + email);
            return userRepository.findByEmail(email);
        }
        log.error("UserService: User not found with email: " + email);
        throw new UserNotFoundException("User not exist");
    }
}
