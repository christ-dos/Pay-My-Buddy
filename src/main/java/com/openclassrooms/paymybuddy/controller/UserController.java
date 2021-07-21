package com.openclassrooms.paymybuddy.controller;

import org.apache.maven.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.IUserService;

import java.util.HashMap;
import java.util.Map;


@RestController
public class UserController {
    private static String userEmail = "lili@email.fr";

    @Autowired
    private IUserService userService;



    @RequestMapping(value="/login")
    public ModelAndView getFriendList(){
        return new ModelAndView();
    }
    @GetMapping("/addfriend")
    public ModelAndView addingFriendListUser(){
        String viewName = "addfriend";
        Map<String,Object> model = new HashMap<>();
        model.put("user", new User());
       // userService.addFriendUser(userEmail, friendEmail);
        return new ModelAndView(viewName,model);
    }

   /* @GetMapping(value = "/user")
    public Optional<User> getUserByEmail(@RequestParam String email){

        return userService.getUserByEmail(email);
    }*/

    @GetMapping(value = "/users")
    public Iterable<User> getUserList(){
        return userService.getUsers();
    }

//    @GetMapping(value = "/friend"){
//
//    }
}
