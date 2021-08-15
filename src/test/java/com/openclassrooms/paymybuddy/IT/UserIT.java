package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

       /*------------------------------------------------------------------------------------------------------------
                                    Integration tests in view addfriend
    --------------------------------------------------------------------------------------------------------------*/
    @Test
    public void showAddFriendViewTest_thenStatusOk() throws Exception {
        //GIVEN
        User user = User.builder()
                .email("fifi@email.fr")
                .firstName("Filipe")
                .lastName("Delarue")
                .build();
        //WHEN
        //THEN
        mockMvc.perform(get("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendLists", "friendList"))
                .andDo(print());
    }

    @Test
    public void showAddFriendViewTest_whenUrlTemplateIsWrong_thenStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/friend")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void addFriendToListConnectionTest_whenUsersExistAndFriendIsNotInListFriend_thenReturnStatusIsOk() throws Exception {
        //GIVEN
        String userEmail = "tela@email.fr";
        User userFriendToAdd = User.builder()
                .email("lili@email.fr").firstName("Elisabeth").lastName("Dupont").build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", userFriendToAdd.getEmail())
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void addFriendToListConnectionTest_whenEmailFriendNotExistInDB_thenReturnErrorInFieldEmailUserNotExistDB() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", friendEmailNotExist))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserNotExistDB"))
                .andDo(print());
    }

    @Test
    public void addFriendToListConnectionTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void addFriendToListConnectionTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "ggpassain@email.fr";
        String userEmail = "tela@email.fr";
        //WHEN

        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", friendEmailAlreadyExist)
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserAlreadyExist"))
                .andDo(print());
    }

    @Test
    public void addFriendToListConnectionTest_whenEmailToAddAndCurrentUserEmailIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UnableAddingOwnEmail"))
                .andDo(print());
    }

    @Test
    public void getListConnectionsTest_whenEmailIsDada_thenReturnListOfFriendList() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/addfriend")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("friendLists"))
//                .andExpect(model().attribute("friendLists",hasItems(iterableWithSize(3))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("ggpassain@email.fr")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Geraldine")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Passain")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("luluM@email.fr")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Lubin")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Mendes")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("lili@email.fr")))))

                .andDo(print());
    }
}
