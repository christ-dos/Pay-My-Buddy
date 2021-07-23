package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Set;

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
        User friendToAdded = getUserByEmail(friendEmail);
        User user = getUserByEmail(userEmail);
        if(friendToAdded == null){
            log.error("UserService: User not found with email: " + friendEmail);
            throw new UserNotFoundException("User not found, please enter a email valid");
        }
       Set<User> listFriend = user.getFriends();
//        if(friendIsPresentInList(listFriend,friendEmail)){
//            log.error("UserService: Friend already exist with email: " + friendEmail);
//            throw  new FriendAlreadyExistException("Friend email already exist, please enter other friend email to add");
//        }
        log.debug("Service: User added with email: " + friendEmail);
        userRepository.saveFriend(userEmail, friendEmail);
    }

    /**
     * Method which get a user by email
     *
     * @param email item unique that permit identify the user
     * @return A user
     *
     * @throws UserNotFoundException if the user is not found in the database
     */
    private User getUserByEmail(String email){
        if(email != null) {
            log.debug("UserService: user found with email: " + email);
            return userRepository.findByEmail(email);
        }
        log.error("UserService: User not found with email: " + email);
        throw new UserNotFoundException("User not exist");
    }

    private Boolean friendIsPresentInList(@NotNull Set<User>listFriend, String friendEmail){
        for (User friend: listFriend){
            if(friend.getEmail().equals(friendEmail)){
                return  true;
            }
        }
        return false;
    }

}
