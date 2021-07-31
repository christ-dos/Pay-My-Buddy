package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.DTO.ReceivingDataTransactionView;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import com.openclassrooms.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
@Slf4j
public class UserController {

    private String userEmail = "dada@email.fr";

    private final List<IFriendList> friendLists = new ArrayList<>();

    private final List<IDisplayingTransaction> transactionslist = new ArrayList<>();

    @Autowired
    private IUserService userService;

    @Autowired
    private ITransactionService transactionService;
//
//
//    @GetMapping(value = "/users")
//    public Iterable<User> getUserList() {
//        return userService.getUsers();
//    }

    @GetMapping("/login")
    public String showLoginView(Model model) {
        log.info("Controller: The View login displaying");

        return "login";
    }

    @GetMapping(value = {"/", "/index"})
    public String showIndexView(@ModelAttribute("receivingDataTransactionView") ReceivingDataTransactionView dataTransactionView, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        model.addAttribute("transactions", transactionService.getTransactionsByEmail(userEmail));
        log.info("Controller: The View index displaying");

        return "index";
    }

    @PostMapping(value = {"/", "/index"})
    public String submitIndexView(@Valid ReceivingDataTransactionView dataTransactionView, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            model.addAttribute("transactions", transactionService.getTransactionsByEmail(userEmail));
            log.error("Controller: Error in fields");
            return "index";
        }
        if (result.getRawFieldValue("friendEmail") == "") {
            result.rejectValue("friendEmail", "", "field friend Email can not be null");
        }
        try {
            transactionService.addTransaction(userEmail,
                    dataTransactionView.getFriendEmail(), dataTransactionView.getAmount(), dataTransactionView.getDescription());
        } catch (BalanceInsufficientException ex) {
            result.rejectValue("amount", "", ex.getMessage());
            log.error("Controller: Insufficient account balance");
        }
        model.addAttribute("transactions", transactionService.getTransactionsByEmail(userEmail));
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("Controller: form index submitted");
        return "index";
    }

    @GetMapping({"/addfriend"})
    public String showAddFriendView(@ModelAttribute("friendList") FriendList friendList, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("Controller: The View addfriend displaying");
        return "addfriend";
    }

    @PostMapping(value = "/addfriend")
    public String submitAddFriend(@Valid FriendList friendList, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("Controller: Error in fields");
            return "addfriend";
        }
        if (friendAlreadyExistsInList(friendList.getEmail())) {
            result.rejectValue("email", "", "This user already exists in your list");
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("Controller: Email already exists in friend list");
            return "addfriend";
        }
        if (!userEmailIsPresentDataBase(friendList.getEmail())) {
            result.rejectValue("email", "", "This user not exist, you can't add it ");
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            log.error("Controller: User Email not exist in data base");
            return "addfriend";
        }
        userService.addFriendUser(userEmail, friendList.getEmail());
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("Controller: form addFriend submitted");

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
        if (userExist == null) {
            return false;
        }
        return true;
    }


}