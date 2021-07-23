package com.openclassrooms.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
//@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvcUser;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private IUserRepository userRepositoryMock;

    /**
     * Method that write an object as JsonString to build the body of the request
     * mock
     *
     * @param obj - The object that we want send in the request
     * @return The value as JsonString of the object
     */
    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("The obj does not be writing", e);
        }
    }

    @Test
    public void submitAddFriendTest_whenUserExist_thenRedirectIntoAddFriendUrl() throws Exception{
        //GIVEN
        String friendEmail = "luciole@email.fr";
        String userEmail = "kikine@email.fr";
        User friend = new User(
                "luciole@email.fr", "monpassword", "Lucinda",
                "Delasalle", 50.00, 256942, null, null);
        doNothing().when(userRepositoryMock).saveFriend(userEmail,friendEmail);
        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(friend);

        //WHEN
        //THEN
            mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                    .content(friendEmail).header("friendEmail", "wiwi@email.fr")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlTemplate("/addfriend"))
                    .andDo(print());
        verify(userServiceMock, times(1)).addFriendUser(anyString(), anyString());
    }

    @Test
    public void submitAddFriendTest_whenUserNotExist_thenThrowsUserNotFoundException() throws Exception{
        //GIVEN
        String friendEmailNotExist ="wiwi@email.fr";
        String userEmail = "kikine@email.fr";
        User  friendNotExist = new User(
                "wiwi@email.fr", "monpassword", "Lucinda",
                "Delasalle", 50.00, 256942, null, null);
        doThrow(
                new UserNotFoundException("User not found, please enter a email valid")).when(userServiceMock).addFriendUser(isA(String.class),isA(String.class));
        //WHEN
        //THEN
//        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
//                .content(friendEmailNotExist)
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect( jsonPath("$.message",
//                      is("User not found, you should input an email that exist in database")))
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
//                .andExpect(result -> assertEquals("User not found, please enter a email valid", result.getResolvedException().getMessage()))
//                .andDo(print());

        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .content(friendEmailNotExist)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect( model().attribute("errorMessage", is("User not found, please enter a email valid"))
                )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found, please enter a email valid", result.getResolvedException().getMessage()))
                .andDo(print());
    }
}
