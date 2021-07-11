package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Builder
@Slf4j
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    /**
     * Method that get the list of users
     * @return An iterable of User
     */
    public Iterable<User> getUsers(){
        return userRepository.findAll();
    }



}
