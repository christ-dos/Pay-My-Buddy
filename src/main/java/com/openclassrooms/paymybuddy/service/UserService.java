package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


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
     *
     */
    @Override
    public void addFriendUser(String userEmail, String friendEmail){
        userRepository.saveFriend(userEmail, friendEmail);
    }

    private Optional<User> getUserByEmail(String email){
        log.debug("UserService: user found with email: " + email);
        return userRepository.findByEmail(email);
    }


}
