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
@Builder
@Slf4j
@AllArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;


    @Override
    public User addUser(String email) {
        return null;
    }

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
