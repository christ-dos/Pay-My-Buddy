package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.EmailNotMatcherException;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
     * Method which add a user to the table User
     *
     * @return A {@link User} Object
     */
    @Override
    public User addUser(AddUser addUser) {
        User userToAddExist = userRepository.findByEmail(addUser.getEmail());
        if (userToAddExist != null) {
            log.error("Service: User's email already exist in DB");
            throw new UserAlreadyExistException("This email already exist");
        }
        if (!addUser.getEmail().equals(addUser.getConfirmEmail())) {
            log.error("Service: confirmEmail not match email");
            throw new EmailNotMatcherException("Confirm mail not mayh with email");
        }
        if (!addUser.getConfirmPassword().equals(addUser.getPassword())) {
            log.error("Service: confirmPassword not match password");
            throw new PasswordNotMatcherException("Confirm not match with password");
        }
        User userToAdd = new User();
        userToAdd.setEmail(addUser.getConfirmEmail());
        userToAdd.setFirstName(addUser.getFirstName());
        userToAdd.setLastName(addUser.getLastName());
        userToAdd.setPassword(addUser.getConfirmPassword());
        userToAdd.setAccountBank(addUser.getAccountBank());

        log.debug("Service: User added : " + addUser.getConfirmEmail());
        return userRepository.save(userToAdd);
    }

    /**
     * Method which update current user to the table User
     *
     * @return A {@link User} Object
     */
    @Override
    public User updateProfile(UpdateCurrentUser updateCurrentUser) {
        User userToUpdate = userRepository.findByEmail(SecurityUtilities.userEmail);
        if (!updateCurrentUser.getConfirmPassword().equals(updateCurrentUser.getPassword())) {
            log.error("Service: confirmPassword not match password");
            throw new PasswordNotMatcherException("Confirm not match with password");
        }
        userToUpdate.setEmail(SecurityUtilities.userEmail);
        userToUpdate.setFirstName(updateCurrentUser.getFirstName());
        userToUpdate.setLastName(updateCurrentUser.getLastName());
        userToUpdate.setPassword(updateCurrentUser.getConfirmPassword());
        log.info("Service:Current user updated");
        return userRepository.save(userToUpdate);
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
     * Method which get list of friend recorded by a user
     *
     * @return A list of {@link FriendList} a DTO model
     * to displaying the email, first name and last name of the friend added
     */
    @Override
    public Page<FriendList> getFriendListByCurrentUserEmailPaged(Pageable pageable) {
        Page<Friend> friendListsByEmail = friendRepository.findByUserEmailOrderByDateAddedDesc(SecurityUtilities.userEmail, pageable);
        int totalElements = (int) friendListsByEmail.getTotalElements();
        log.debug("UserService: friend list  paged found for current user: " + SecurityUtilities.userEmail);

        return new PageImpl<FriendList>(friendListsByEmail.stream()
                .map(friend -> {
                    User user = userRepository.findByEmail(friend.getFriendEmail());
                    return new FriendList(user.getEmail(), user.getFirstName(), user.getLastName());
                }).collect(Collectors.toList()), pageable, totalElements);
    }

    @Override
    public List<FriendList> getFriendListByCurrentUserEmail() {
        Page<Friend> friendListsByEmailPaged = friendRepository.findByUserEmailOrderByDateAddedDesc(SecurityUtilities.userEmail, null);
        List<Friend> friendListsByEmail = friendListsByEmailPaged.getContent();
        log.debug("UserService: friend list found for current user: " + SecurityUtilities.userEmail);

        return friendListsByEmail.stream()
                .map(friend -> {
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
        Page<Friend> listFriendPaged = friendRepository.findByUserEmailOrderByDateAddedDesc(SecurityUtilities.userEmail, null);
        List<Friend> listFriends = listFriendPaged.getContent();
        for (Friend friend : listFriends) {
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
