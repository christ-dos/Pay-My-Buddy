package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class UserTestIT {

    @Autowired
    private MockMvc mockMvcUser;

    @MockBean
    private IUserRepository userRepository;


    @MockBean
    private UserService userService;

    @Test
    public void showAddFriendViewTest_thenStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend"))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attribute("user",new User()))
                .andDo(print());
    }

    @Test
    public void showAddFriendViewTest_whenUrlTemplateIsWrong_thenStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/friend"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenUsersExist_thenReturnStatusRedirectionSuccess() throws Exception {
        //GIVEN
        String friendEmail = "luciole@email.fr";
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .content((friendEmail)).header("friendEmail", "luciole@email.fr")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenEmailFriendNotExist_thenThrowsUserNotFoundException() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        User user = new User(
                "wiwi@email.fr", "monSuperpassword",
                "Wiliam", "Delarue", 10.00, 920476, null, null);
      doThrow(
                new UserNotFoundException("User not found, please enter a email valid")).when(userService).addFriendUser(isA(String.class),isA(String.class));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .content(friendEmailNotExist)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect( model().attribute("errorMessage", is("User not found, please enter a email valid"))
                )
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> Assertions.assertEquals("User not found, please enter a email valid", result.getResolvedException().getMessage()))
                .andDo(print());
    }

}
