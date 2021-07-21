package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.IUserService;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;



@RestController
@Slf4j
public class UserController {

    private final static String userEmail = "dada@email.fr";

    @Autowired
    private IUserService userService ;


    @RequestMapping(value="/login")
    public ModelAndView getFriendList(){
        return new ModelAndView();
    }

    @GetMapping("/addfriend")
    public ModelAndView showAddFriendView(){
        String viewName = "addfriend";
        Map<String,Object> model = new HashMap<>();
        model.put("user", new User());
        log.info("The View addfriend displaying");
        return new ModelAndView(viewName, model);
    }

    @PostMapping("/addfriend")
    public ModelAndView submitAddFriend(@ModelAttribute("friends") String friendEmail){

        userService.addFriendUser(userEmail, friendEmail);
        log.info("form submitted");
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/addfriend");

        return new ModelAndView(redirectView);
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
