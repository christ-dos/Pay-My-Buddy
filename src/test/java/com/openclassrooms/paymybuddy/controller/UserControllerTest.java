package com.openclassrooms.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RunWith(SpringRunner.class)
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
    public void submitAddFriendTest_whenUserExist_thenRedirectIntoaddfrienrUrl() throws Exception{
        //GIVEN
        String friendEmail = "luciole@email.fr";
        String userEmail = "kikine@email.fr";
        Optional<User> optionalUser = Optional.of(new User("luciole@email.fr", "monpassword", "Lucinda", "Delasalle", 50.00, 256942, null, null));
        doNothing().when(userRepositoryMock).saveFriend(userEmail,friendEmail);
        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(optionalUser);

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
        String friendEmailNotExist = "wiwi@email.fr";
        String userEmail = "kikine@email.fr";
        Optional<User> optionalUser = Optional.of(new User("wiwi@email.fr", "monpassword", "Lucinda", "Delasalle", 50.00, 256942, null, null));
        doNothing().when(userRepositoryMock).saveFriend(userEmail,friendEmailNotExist);
        when(userRepositoryMock.findByEmail(friendEmailNotExist)).thenReturn(optionalUser);

        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .content(friendEmailNotExist).header("friendEmail", "wiwi@email.fr")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect( jsonPath("$.message",
                        is("User not found")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found", result.getResolvedException().getMessage()))
                .andDo(print());
        verify(userServiceMock, times(1)).addFriendUser(anyString(), anyString());
    }
}
