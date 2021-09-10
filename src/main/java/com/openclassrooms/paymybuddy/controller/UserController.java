package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.EmailNotMatcherException;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import com.openclassrooms.paymybuddy.service.IUserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

/**
 * Class Controller that manage user's requests
 *
 * @author Christine Duarte
 */
@Controller
@Slf4j
public class UserController {
    /**
     * Dependency  {@link IUserService} injected
     */
    @Autowired
    private IUserService userService;

    /**
     * Dependency  {@link ITransactionService} injected
     */
    @Autowired
    private ITransactionService transactionService;


/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View signup
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method POST which create a new User in table User
     *
     * @param addUser A model DTO {@link AddUser} that permit get information to create a new User
     * @param result  An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model   Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
    @PostMapping("/signup")
    public String signUpUserViewSignUp(@Valid @ModelAttribute("addUser") AddUser addUser
            , BindingResult result, Model model) {
        if (result.hasFieldErrors()) {
            model.addAttribute("addUser", addUser);
            log.error("Controller: Error in fields");
            return "signup";
        }
        try {
            userService.addUser(addUser);
        } catch (UserAlreadyExistException ex) {
            log.error("Controller: There is already a user registered with the email provided");
            result.rejectValue("email", "EmailAlreadyExist", ex.getMessage());
            return "signup";
        } catch (EmailNotMatcherException ex1) {
            log.error("Controller: ConfirmEmail not match with email");
            result.rejectValue("confirmEmail", "ConfirmEmailNotMatcher", ex1.getMessage());
            return "signup";
        } catch (PasswordNotMatcherException ex2) {
            log.error("Controller: ConfirmPassword not match with password");
            result.rejectValue("confirmPassword", "ConfirmPasswordNotMatcher", ex2.getMessage());
            return "signup";
        }
        model.addAttribute("addUser", addUser);
        model.addAttribute("message", "Account registered with success!");
        log.debug("Controller: User Added with success: " + addUser.getConfirmEmail());

        return "login";
    }

    /**
     * Method GET to displaying the view signup to create a new User mapped in "/signup"
     *
     * @param addUser A model DTO {@link AddUser} that permit get information to create a new User
     * @return A String containing the name of view
     */
    @GetMapping("/signup")
    public String getSignUpView(@ModelAttribute("addUser") AddUser addUser) {
        log.info("Controller: The View Sign Up displaying");

        return "signup";
    }
/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View login
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method GET to displaying the view login for log  mapped in "/login"
     *
     * @return A String containing the name of view
     */
    @GetMapping("/login")
    public String getLoginView() {
        log.info("Controller: The View login displaying");

        return "login";
    }
/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View home
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method GET to displaying the view for home in endpoint in "/home"
     *
     * @param model Interface that defines a support for model attributes
     * @param page  An Optional with the page
     * @param size  An optional with the number of items in page
     * @return A String containing the name of view
     */
    @GetMapping("/home")
    public String getUserInformationHomeView(Model model, @RequestParam("page") Optional<Integer> page,
                                             @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

        FriendList lastFriendAdded = null;
        DisplayingTransaction lastTransaction = null;
        try {
            lastFriendAdded = userService.getFriendListByCurrentUserEmail().get(0);
            lastTransaction = transactionService.getCurrentUserTransactionsByEmail(PageRequest.of(currentPage - 1, pageSize)).getContent().get(0);
        } catch (IndexOutOfBoundsException ex) {
            log.error("Controller: Empty list");
        }
        log.info("Controller: The View home displaying");
        model.addAttribute("user", userService.getUserByEmail(SecurityUtilities.getCurrentUser()));
        model.addAttribute("lastBuddy", lastFriendAdded);
        model.addAttribute("lastTransaction", lastTransaction);

        return "home";
    }
/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View contact
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method GET to displaying the view for contact  in endpoint in "/contact"
     *
     * @return A String containing the name of view
     */
    @GetMapping("/contact")
    public String getContactView() {
        log.info("Controller: The View contact displaying");

        return "contact";
    }
/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View profile
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method POST to collect information in the view profile to update currentUser profile in endpoint in "/profile"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
    @PostMapping("/profile")
    public String updateCurrentUserInformation(@Valid @ModelAttribute("updateCurrentUser") UpdateCurrentUser updateCurrentUser,
                                               BindingResult result, Model model) {
        if (result.hasFieldErrors()) {
            model.addAttribute("updateCurrentUser", updateCurrentUser);
            model.addAttribute("currentUser", userService.getUserByEmail(SecurityUtilities.getCurrentUser()));
            log.error("Controller: Error in fields");
            return "profile";
        }
        try {
            userService.updateProfile(updateCurrentUser);
        } catch (PasswordNotMatcherException ex) {
            log.error("Controller: Confirm not match with password");
            result.rejectValue("confirmPassword", "ConfirmPasswordNotMatch", ex.getMessage());
        }
        model.addAttribute("updateCurrentUser", updateCurrentUser);
        model.addAttribute("message", "Profile has been updated");
        model.addAttribute("currentUser", userService.getUserByEmail(SecurityUtilities.getCurrentUser()));
        log.debug("Controller: profile updated:" + SecurityUtilities.getCurrentUser());

        return "profile";
    }

    /**
     * Method GET to displaying the view for profile  in endpoint in "/profile"
     *
     * @param model Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
    @GetMapping("/profile")
    public String getCurrentUserInformationInProfileView(@ModelAttribute("updateCurrentUser") UpdateCurrentUser updateCurrentUser,
                                                         @ModelAttribute("currentUser") User currentUser,
                                                         Model model) {
        User userByEmail = userService.getUserByEmail(SecurityUtilities.getCurrentUser());
        userByEmail.setPassword(userByEmail.getPassword());
        model.addAttribute("currentUser", userByEmail);
        model.addAttribute("updateCurrentUser", updateCurrentUser);
        log.info("Controller: The View profile displaying");

        return "profile";
    }
/**
 * ------------------------------------------------------------------------------------------------------
 *                                         View addfriend
 * ------------------------------------------------------------------------------------------------------
 */
    /**
     * Method POST that process data receiving by the view addfriend in endpoint "/addfriend"
     * for adding a friend in table friend in database
     *
     * @param friendList DTO model {@link FriendList }that permit displaying the list od friends
     * @param result     An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model      Interface that defines a support for model attributes
     * @param page       An Optional with the page
     * @param size       An optional with the number of items in page
     * @return A String containing the name of view
     */
    @PostMapping(value = "/addfriend")
    public String addFriendToListConnection(@Valid FriendList friendList, BindingResult result, Model model,
                                            @RequestParam("page") Optional<Integer> page,
                                            @RequestParam("size") Optional<Integer> size) {
        if (result.hasErrors()) {
            getModelsAddFriends(model, page, size);
            log.error("Controller: Error in fields");
            return "addfriend";
        }
        if (Objects.requireNonNull(result.getRawFieldValue("email")).equals(SecurityUtilities.getCurrentUser())) {
            result.rejectValue("email", "UnableAddingOwnEmail", "Unable add own email in your Connections");
            getModelsAddFriends(model, page, size);
            log.error("Controller: Invalid addition with email: " + SecurityUtilities.getCurrentUser());
            return "addfriend";
        }
        try {
            userService.addFriendCurrentUserList(friendList.getEmail());
        } catch (UserAlreadyExistException e1) {
            result.rejectValue("email", "UserAlreadyExist", e1.getMessage());
            log.error("Controller: User already exist in your list");
        } catch (UserNotFoundException e2) {
            result.rejectValue("email", "UserNotExistDB", e2.getMessage());
            log.error("Controller: User Email not exist in data base");
        }
        getModelsAddFriends(model, page, size);
        log.info("Controller: form addFriend submitted");

        return "addfriend";
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
        getModelsAddFriends(model, page, size);
        log.info("Controller: The View addfriend displaying");

        return "addfriend";
    }

    /**
     * Method private that get models for pagination of friendList
     *
     * @param model Interface that defines a support for model attributes.
     * @param page  An Optional with the page
     * @param size  An optional with the nomber of items in page
     */
    private void getModelsAddFriends(Model model, Optional<Integer> page, Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

        Page<FriendList> friendLists = userService.getFriendListByCurrentUserEmailPaged(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("friendLists", friendLists);
        model.addAttribute("totalPages", friendLists.getTotalPages());
        model.addAttribute("currentPage", currentPage);
    }
}