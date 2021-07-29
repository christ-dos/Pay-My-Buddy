package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
@Slf4j
public class UserController {

    private final static String userEmail = "dada@email.fr";

    private List<IFriendList> friendLists = new ArrayList<>();

    @Autowired
    private IUserService userService;
//
//
//    @GetMapping(value = "/users")
//    public Iterable<User> getUserList() {
//        return userService.getUsers();
//    }

    @GetMapping("/login")
    public ModelAndView showLoginView() {
        log.info("The View login displaying");
        return new ModelAndView();
    }

    @GetMapping("/index")
    public ModelAndView showViewIndex(Model model) {
        //model.addAttribute("transaction", new TransDTO());
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("The View index displaying");

        return new ModelAndView();
    }

    @PostMapping(value = "/index")
    public String submitAddFriend(Model model){

        return "index";
    }

    @GetMapping({"/addfriend"})
    public String showAddFriendView(@ModelAttribute("friendList") FriendList friendList, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));

        return "addfriend";
    }

    @PostMapping(value = "/addfriend")
    public String submitAddFriend(@Valid FriendList friendList, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("Error in fields");
            return "addfriend";
        }
        if (friendAlreadyExistsInList(friendList.getEmail())) {
            result.rejectValue("email", "", "This user already exists in your list");
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("Email already exists in friend list");
            return "addfriend";
        }
        if (!userEmailIsPresentDataBase(friendList.getEmail())) {
            result.rejectValue("email", "", "This user not exist, you can't add it ");
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("User Email not exist in data base");
            return "addfriend";
        }
        userService.addFriendUser(userEmail, friendList.getEmail());
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("form submitted");

        return "addfriend";
    }

    private Boolean friendAlreadyExistsInList(String friendEmail) {
        Set<IFriendList> listFriend = userService.getFriendListByEmail(userEmail);
        for (IFriendList friend : listFriend) {
            if (friend.getEmail().equals(friendEmail)) {
                return true;
            }
        }
        return false;
    }

    private Boolean userEmailIsPresentDataBase(String friendEmail) {
        User userExist = userService.getUserByEmail(friendEmail);
        if(userExist == null) {
            return false;
        }
        return true;
    }


}