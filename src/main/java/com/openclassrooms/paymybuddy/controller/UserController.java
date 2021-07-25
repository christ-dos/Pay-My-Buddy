package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.IUserService;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class UserController {

    private final static String userEmail = "dada@email.fr";
    private List<IFriendList> friendLists = new ArrayList<>();

    @Autowired
    private IUserService userService;


    @GetMapping("/login")
    public ModelAndView showViewLogin() {
        String viewName = "login";
        log.info("The View login displaying");
        return new ModelAndView();
    }

    @GetMapping("/index")
    public ModelAndView showViewIndex(Model model) {
        IFriendList friend1 =  new FriendList("christ@email.fr","christ","dodo");
        IFriendList friend2 =  new FriendList("sylvie@email.fr","sylvie","duddu");
        friendLists.add(friend1);
        friendLists.add(friend2);

        String viewName = "index";
//        Map<String, Object> model = new HashMap<>();
       // Set<IFriendList> friendSetByUser = userService.getFriendListByEmail(userEmail);
       // friendLists = friendSetByUser.stream().collect(Collectors.toList());

        //model.addAttribute("transaction", new TransDTO());
        model.addAttribute("friendLists", friendLists);

        log.info("The View index displaying");
        return new ModelAndView();
    }

    @GetMapping("/addfriend")
    public ModelAndView showAddFriendView() {
        String viewName = "addfriend";

        Map<String, Object> model = new HashMap<>();
        Set<IFriendList> friendSetByUser = userService.getFriendListByEmail(userEmail);
        friendLists = friendSetByUser.stream().collect(Collectors.toList());

        model.put("user", new User());
        model.put("friendLists", friendLists);

        log.info("The View addfriend displaying");

        return new ModelAndView(viewName, model);
    }


    @SneakyThrows
    @PostMapping(value = "/addfriend")
    public ModelAndView submitAddFriend(@Valid @ModelAttribute("friends") String friendEmail, BindingResult result) {
        String viewName = "addfriend";
        userService.addFriendUser(userEmail, friendEmail);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/addfriend");
        log.info("form submitted");

        return new ModelAndView(redirectView);
    }


   /* @GetMapping(value = "/user")
    public Optional<User> getUserByEmail(@RequestParam String email){

        return userService.getUserByEmail(email);
    }

    @GetMapping(value = "/users")
    public Iterable<User> getUserList() {
        return userService.getUsers();
    }

    */

}
