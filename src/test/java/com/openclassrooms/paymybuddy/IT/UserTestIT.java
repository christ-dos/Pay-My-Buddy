package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
        User user = User.builder()
                .email("fifi@email.fr")
                .firstName("Filipe")
                .lastName("Delarue")
                .build();
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend")
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
        mockMvcUser.perform(get("/friend"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenUsersExistAndFriendIsNotInListFriend_thenReturnStatusIsOk() throws Exception {
        //GIVEN
        String userEmail = "tela@email.fr";
        User userToAdd = User.builder()
                .email("lili@email.fr").firstName("Elisabeth").lastName("Dupont").build();

        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .param("email", userToAdd.getEmail())
                .param("userEmail",userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenEmailFriendNotExistInDB_thenReturnErrorInFieldEmailUserNotExist() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .param("email", friendEmailNotExist))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrors("friendList", "email"))
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("friendList", "email"))
                .andDo(print());
    }

    @Test
    public void saveFriendTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "ggpassain@email.fr";
        String userEmail = "tela@email.fr";
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                .param("email",friendEmailAlreadyExist)
                .param("userEmail",userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                //.andExpect(model().attribute("friendList",new FriendList("sara@email.fr",null,null)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andDo(print());
    }

}
