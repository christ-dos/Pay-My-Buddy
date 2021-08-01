package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.DTO.ReceivingDataTransactionView;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
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
import java.util.Set;

/**
 * Class Controller that manage requests of views
 *
 * @author Christine Duarte
 */
@Controller
@Slf4j
public class UserController {
    /**
     * The Current User Connected
     */
    private final static String userEmail = "dada@email.fr";

    //private final List<IFriendList> friendLists = new ArrayList<>();

    //private final List<IDisplayingTransaction> transactionslist = new ArrayList<>();
    /**
     * Dependency  {@link IUserService } injected
     */
    @Autowired
    private IUserService userService;

    /**
     * Dependency {@link ITransactionService } injected
     */
    @Autowired
    private ITransactionService transactionService;
//
//
//    @GetMapping(value = "/users")
//    public Iterable<User> getUserList() {
//        return userService.getUsers();
//    }

    /**
     * Method GET to displaying the view for log-in mapped in "/login"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
    @GetMapping("/login")
    public String showLoginView(Model model) {
        log.info("Controller: The View login displaying");

        return "login";
    }

    /**
     * Method GET to displaying the view index mapped in "/" and "/index"
     *
     * @param dataTransactionView A model DTO {@link ReceivingDataTransactionView} that displaying transactions
     *                            informations in view : receiver's name, reason of transaction and amount
     * @param model               Interface that defines a support for model attributes.
     * @return A String containing the name of view
     */
    @GetMapping(value = {"/", "/index"})
    public String showIndexView(@ModelAttribute("receivingDataTransactionView") ReceivingDataTransactionView dataTransactionView, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        model.addAttribute("transactions", transactionService.getTransactionsByEmail(userEmail));
        log.info("Controller: The View index displaying");

        return "index";
    }

    /**
     * Method POST that process data receiving by the view index in endpoint "/index" and "/"
     * for adding transactions in table transaction in database
     *
     * @param dataTransactionView A model DTO {@link ReceivingDataTransactionView} that displaying transactions
     *                            informations in view : receiver's name, reason of transaction and amount
     * @param result              An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model               Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
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

    /**
     * Method GET to displaying the view addfriend mapped in "/addfriend"
     *
     * @param friendList DTO model {@link FriendList }that permit displaying the list od friends
     * @param model      Interface that defines a support for model attributes.
     * @return A String containing the name of view
     */
    @GetMapping({"/addfriend"})
    public String showAddFriendView(@ModelAttribute("friendList") FriendList friendList, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
        log.info("Controller: The View addfriend displaying");
        return "addfriend";
    }

    /**
     * Method POST that process data receiving by the view addfriend in endpoint "/addfriend"
     * for adding a friend in table friend in database
     *
     * @param friendList DTO model {@link FriendList }that permit displaying the list od friends
     * @param result     An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model      Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
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

    /**
     * Private method that verify if the friend already exist in the list
     *
     * @param friendEmail A string containing the email of the friend
     * @return true if the friend already exist in list else return false
     */
    private Boolean friendAlreadyExistsInList(String friendEmail) {
        Set<IFriendList> listFriend = userService.getFriendListByEmail(userEmail);
        for (IFriendList friend : listFriend) {
            if (friend.getEmail().equals(friendEmail)) {
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
        if (userService.getUserByEmail(friendEmail) == null) {
            return false;
        }
        return true;
    }

}