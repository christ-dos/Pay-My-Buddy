package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
     * Method which add a User friend to the table friend
     *
     * @param userEmail   Email of the user which want added a friend to the list
     * @param friendEmail email of the friend that will be added to the list
     * @return A user Object
     */
    @Override
    public Friend addFriendUser(String userEmail, String friendEmail) {
        User friend = userRepository.findByEmail(friendEmail);

        Friend friendToAdd = new Friend(userEmail, friendEmail, LocalDateTime.now());

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
     * @param userEmail A String containing the email of the user which want added a friend in his list
     * @return A set of {@link IFriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    @Override
    public List<FriendList> getFriendListByEmail(String userEmail) {
       List<Friend> friendLists = friendRepository.findByUserEmailOrderByDateAddedDesc(userEmail);

        return friendLists.stream().map(friend -> {
            User user = userRepository.findByEmail(friend.getFriendEmail());
            FriendList friendList = new FriendList(user.getEmail(),user.getFirstName(),user.getLastName());
            return friendList;
        }).collect(Collectors.toList());


//        List<FriendList> friends = new ArrayList<>();
//        User user = userRepository.findByEmail(userEmail);
//        log.debug("UserService: User friend found for email: " + userEmail);
//
//        return friends = user.getFriends().stream()
//                .map(friend -> new FriendList(friend.getEmail(), friend.getFirstName(), friend.getLastName())).collect(Collectors.toList());
    }

//    List<User> friends = new ArrayList<>(user.getFriends());
//        friends.add(friend);
//        user.setFriends(friends);

}
