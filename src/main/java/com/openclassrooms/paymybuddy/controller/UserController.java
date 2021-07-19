package com.openclassrooms.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.IUserService;


@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value="/login")
    public ModelAndView getFriendList(){
        return new ModelAndView();
    }
    @RequestMapping(value="/addfriend")
    public ModelAndView addFriendList(){
        return new ModelAndView();
    }

   /* @GetMapping(value = "/user")
    public Optional<User> getUserByEmail(@RequestParam String email){

        return userService.getUserByEmail(email);
    }*/

    @GetMapping(value = "/users")
    public Iterable<User> getUserList(){
        return userService.getUsers();
    }

    @GetMapping(value = "/friend")
    public void addingFriendListUser(@Param("userEmail") String userEmail ,@Param("friendEmail") String friendEmail){
        userService.addFriendUser(userEmail, friendEmail);
    }
}
