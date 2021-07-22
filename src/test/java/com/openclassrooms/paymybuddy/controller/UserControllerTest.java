package com.openclassrooms.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
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
    public void submitAddFriendTest() throws Exception{
        //GIVEN
        String friendEmail = "luciole@email.fr";
        String userEmail = "kikine@email.fr";
        //WHEN
        //THEN
            mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                    .param("userEmail",userEmail)
                    .param("friendEmail",friendEmail)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/addfriend"))
                    .andDo(print());
        verify(userServiceMock, times(1)).addFriendUser(anyString(), anyString());
    }
}
