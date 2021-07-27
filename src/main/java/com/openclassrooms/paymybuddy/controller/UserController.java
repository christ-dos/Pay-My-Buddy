package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
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
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
public class UserController implements WebMvcConfigurer {

    private final static String userEmail = "dada@email.fr";
    private List<IFriendList> friendLists = new ArrayList<>();

    @Autowired
    private IUserService userService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

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



//        @PostMapping(value = "/addfriend")
//    public ModelAndView submitAddFriend(@ModelAttribute("email") @Valid String friendEmail, BindingResult result, Model model) {
//        if(result.hasFieldErrors()){
//            return new ModelAndView();
//        }
//        model.addAttribute("user", new User());
//        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
//        userService.addFriendUser(userEmail, friendEmail);
//        RedirectView redirectView = new RedirectView();
//        redirectView.setUrl("/addfriend");
//        log.info("form submitted");
//
//        return new ModelAndView(redirectView);
//    }

    @GetMapping({"/addfriend"})
    public String showAddFriendView(@ModelAttribute("friendList") FriendList friendList, Model model) {
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));

        return "addfriend";
    }

    @PostMapping(value = "/addfriend")
    public String submitAddFriend(@Valid FriendList friendList, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            return "addfriend";
        }
        if (result.hasGlobalErrors()) {
            model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
            return "addfriend";
        }
//        try {
        userService.addFriendUser(userEmail, friendList.getEmail());
        model.addAttribute("friendLists", userService.getFriendListByEmail(userEmail));
//        } catch (UserNotFoundException ex) {
//            model.addAttribute("userNotFound", ex.getMessage());
//        }
        log.info("form submitted");

        return "redirect:/addfriend";
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
