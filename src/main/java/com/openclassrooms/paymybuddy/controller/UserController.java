package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateProfile;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.configuration.MyUserDetails;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import com.openclassrooms.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Class Controller that manage users'requests
 *
 * @author Christine Duarte
 */
@Controller
@Slf4j
public class UserController {


    //private final List<IFriendList> friendLists = new ArrayList<>();

    //private final List<IDisplayingTransaction> transactionslist = new ArrayList<>();
    /**
     * Dependency  {@link IUserService } injected
     */
    @Autowired
    private IUserService userService;

    @Autowired
    private ITransactionService transactionService;

//    @Autowired
//    private UserDetailsService userDetailsService;

    /**
     * Method that get all users
     *
     * @return An Iterable of User object
     */
    @GetMapping(value = "/users")
    public Iterable<User> getUserList() {
        return userService.getUsers();
    }

    /**
     * Method GET to displaying the view for log in mapped in "/login"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
//    @RolesAllowed({"USER"})
    @GetMapping("/login")
    public String showLoginView(@ModelAttribute("userDetails") MyUserDetails userDetails, Model model) {
        log.info("Controller: The View login displaying");

        return "login";
    }

    //    @RolesAllowed({"USER", "ADMIN"})
    @PostMapping("/login")
    public String submitLoginView(@Valid MyUserDetails userDetails, BindingResult result, Model model) {

        if (result.hasErrors()) {
//            model.addAttribute("userDetails", "Bad credentials");
            log.error("Controller: Error in fields");
//            return "login";
        }
        try {
//            userDetailsService.loadUserByUsername(userDetails.getUsername());
        } catch (UsernameNotFoundException ex) {
            result.rejectValue("username", "UserNameNotFound", ex.getMessage());
            log.error("Controller: Username Not found");
            return "login";
        }

        return "transaction";

    }

    /**
     * Method GET to displaying the view for home in endpoint in "/home"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
//    @RolesAllowed({"USER"})
    @GetMapping("/home")
    public String getUserInformationHomeView(@ModelAttribute("user") User user, Model model, @RequestParam("page") Optional<Integer> page,
                                             @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);

        FriendList lastFriendAdded = null;
        DisplayingTransaction lastTransaction = null;
        try {
            lastFriendAdded = userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize)).getContent().get(0);
            lastTransaction = transactionService.getCurrentUserTransactionsByEmail().get(0);
        } catch (IndexOutOfBoundsException ex) {
            log.error("Controller: Empty list");
        }
        log.info("Controller: The View home displaying");
        model.addAttribute("user", userService.getUserByEmail(SecurityUtilities.userEmail));
        model.addAttribute("lastBuddy", lastFriendAdded);
        model.addAttribute("lastTransaction", lastTransaction);

        return "home";
    }

    /**
     * Method GET to displaying the view for contact  in endpoint in "/contact"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
//    @RolesAllowed({"USER"})
    @GetMapping("/contact")
    public String getContactView(Model model) {
        log.info("Controller: The View contact displaying");

        return "contact";
    }

    /**
     * Method GET to displaying the view for profile  in endpoint in "/profile"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
//    @RolesAllowed({"USER"})
    @GetMapping("/profile")
    public String getCurrentUserInformationInProfileView(@ModelAttribute("updateProfile") UpdateProfile updateProfile, @ModelAttribute("currentUser") User currentUser,
                                                         Model model) {
        model.addAttribute("currentUser", userService.getUserByEmail(SecurityUtilities.userEmail));
        model.addAttribute("updateProfile", updateProfile);
        log.info("Controller: The View profile displaying");

        return "profile";
    }

    /**
     * Method POST to collect information in the view profile to update currentUser profile in endpoint in "/profile"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
//    @RolesAllowed({"USER"})
    @PostMapping("/profile")
    public String updateCurrentUserInformation(@Valid @ModelAttribute("updateProfile") UpdateProfile updateProfile, BindingResult result, Model model) {
        if (result.hasFieldErrors()) {
            model.addAttribute("updateProfile", updateProfile);
            model.addAttribute("currentUser", userService.getUserByEmail(SecurityUtilities.userEmail));
            log.error("Controller: Error in fields");
            return "profile";
        }
        try {
            userService.addUser(updateProfile);
        } catch (PasswordNotMatcherException ex) {
            log.error("Controller: Confirm not match with password");
            result.rejectValue("confirmPassword", "ConfirmPasswordNotMatch", ex.getMessage());
        }
        model.addAttribute("updateProfile", updateProfile);
        model.addAttribute("currentUser", userService.getUserByEmail(SecurityUtilities.userEmail));
        log.debug("Controller: profile updated:" + SecurityUtilities.userEmail);

        return "profile";
    }

    /**
     * Method GET to displaying the view addfriend mapped in "/addfriend"
     *
     * @param friendList DTO model {@link FriendList }that permit displaying the list od friends
     * @param model      Interface that defines a support for model attributes.
     * @return A String containing the name of view
     */
    @GetMapping({"/addfriend"})
    public String getListConnections(@ModelAttribute("friendList") FriendList friendList, Model model,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);

        Page<FriendList> friendLists = userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize));

        model.addAttribute("friendLists", friendLists);
        model.addAttribute("totalPages", friendLists.getTotalPages());
        model.addAttribute("currentPage", page);

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
    public String addFriendToListConnection(@Valid FriendList friendList, BindingResult result, Model model, @RequestParam("page") Optional<Integer> page,
                                            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);

        Page<FriendList> friendLists = userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize));

        if (result.hasErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize)));
            model.addAttribute("totalPages", friendLists.getTotalPages());
            model.addAttribute("currentPage", page);
            log.error("Controller: Error in fields");
            return "addfriend";
        }
        if (result.getRawFieldValue("email").equals(SecurityUtilities.userEmail)) {
            result.rejectValue("email", "UnableAddingOwnEmail", "Unable add own email in your Connections");
            model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize)));
            model.addAttribute("totalPages", friendLists.getTotalPages());
            model.addAttribute("currentPage", page);
            log.error("Controller: Invalid addition with email: " + SecurityUtilities.userEmail);
            return "addfriend";
        }
        try {
            userService.addFriendCurrentUserList(friendList.getEmail(), PageRequest.of(currentPage, pageSize));
        } catch (UserAlreadyExistException e1) {
            result.rejectValue("email", "UserAlreadyExist", e1.getMessage());
            log.error("Controller: User already exist in your list");
        } catch (UserNotFoundException e2) {
            result.rejectValue("email", "UserNotExistDB", e2.getMessage());
            log.error("Controller: User Email not exist in data base");
        }
        model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail(PageRequest.of(currentPage, pageSize)));
        model.addAttribute("totalPages", friendLists.getTotalPages());
        model.addAttribute("currentPage", page);
        log.info("Controller: form addFriend submitted");

        return "addfriend";
    }


}