package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.jws.WebService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTestIT {

    @Autowired
    private MockMvc mockMvcUser;

    @Test
    public void saveFriendTest() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addfriend"))
                .andDo(print());


    }


}
