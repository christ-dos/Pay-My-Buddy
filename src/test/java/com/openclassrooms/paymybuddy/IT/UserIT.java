package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateProfile;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
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
                                    Integration tests view home
    --------------------------------------------------------------------------------------------------------------*/
    @Test
    public void getUserInformationHomeViewTest_whenCurrentUserIsDada_thenReturnFirstNameDamienAndLastNameSanchez() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/home")
                        .param("email", SecurityUtilities.userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user","lastBuddy","lastTransaction"))
                .andExpect(model().attribute("user",hasProperty("email", Matchers.is("dada@email.fr"))))
                .andExpect(model().attribute("user",hasProperty("balance", Matchers.is(200.0))))
                .andExpect(model().attribute("user",hasProperty("firstName", Matchers.is("Damien"))))
                .andExpect(model().attribute("user",hasProperty("lastName", Matchers.is("Sanches"))))
                .andExpect(model().attribute("lastBuddy",hasProperty("firstName", Matchers.is("Elisabeth"))))
                .andExpect(model().attribute("lastBuddy",hasProperty("lastName", Matchers.is("Dupond"))))
                .andExpect(model().attribute("lastTransaction",hasProperty("firstName", Matchers.is("Lubin"))))
                .andExpect(model().attribute("lastTransaction",hasProperty("amount", Matchers.is(-15.0))))
                .andDo(print());
    }

    @Test
    public void getUserInformationHomeViewTest_whenListFriendOrListTransactionIsEmpty_thenDisplayingHomeViewNoneResult() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("emptylist@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("passpass")
                .balance(100.0)
                .build();
        //WHEN
        //THEN
        mockMvc.perform(get("/home")
                        .param("email", String.valueOf(currentUser.getEmail())))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user",hasProperty("email", Matchers.is("dada@email.fr"))))
                .andExpect(model().attribute("user",hasProperty("balance", Matchers.is(200.0))))
                .andExpect(model().attribute("user",hasProperty("firstName", Matchers.is("Damien"))))
                .andExpect(model().attribute("user",hasProperty("lastName", Matchers.is("Sanches"))))
                .andExpect(model().attribute("lastBuddy",nullValue()))
                .andExpect(model().attribute("lastTransaction",nullValue()))
                .andDo(print());
    }


    /*------------------------------------------------------------------------------------------------------------
                                    Integration tests view addfriend
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
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("ggpassain@email.fr")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Geraldine")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Passain")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("luluM@email.fr")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Lubin")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Mendes")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("lili@email.fr")))))

                .andDo(print());
    }
      /*------------------------------------------------------------------------------------------------------------
                                    Integration tests  view profile
    --------------------------------------------------------------------------------------------------------------*/
      @Test
      public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfileAndGood_thenReturnStatusOK() throws Exception {
          //GIVEN
          //WHEN
          //THEN
          mockMvc.perform(get("/profile")
                          .param("email", SecurityUtilities.userEmail))
                  .andExpect(status().isOk())
                  .andExpect(view().name("profile"))
                  .andExpect(model().size(2))
                  .andExpect(model().hasNoErrors())
                  .andExpect(model().attributeExists("currentUser", "updateProfile"))
                  .andExpect(model().attribute("currentUser", hasProperty("email", is("dada@email.fr"))))
                  .andExpect(model().attribute("currentUser", hasProperty("firstName", is("Damien"))))
                  .andExpect(model().attribute("currentUser", hasProperty("lastName", is("Sanches"))))
                  .andExpect(model().attribute("currentUser", hasProperty("password", is("passpasspass"))))
                  .andDo(print());
      }

    @WithMockUser(value = "spring")
    @Test
    public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/prof"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIsDada_thenReturnUserDadaUpdated() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanches");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "dada@email.fr")
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attribute("updateProfile", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("updateProfile", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("updateProfile", hasProperty("lastName", is("Sanches"))))
                .andExpect(model().attribute("updateProfile", hasProperty("password", is("passpasspass"))))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenFirstNameIsBlank_thenReturnFieldErrorNotBlankInFieldFirstName() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "firstName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenLastNameIsBlank_thenReturnFieldErrorNotBlankInFieldLastName() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "lastName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsLess8_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("pass");
        updateProfileCurrentUser.setConfirmPassword("pass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIsDadaAndPassWordIsGreaterThan30_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspasspasspasspasspasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspasspasspasspasspasspass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("        ");
        updateProfileCurrentUser.setConfirmPassword("passpass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "password", "NotBlank"))
                .andDo(print());

    }

    @Test
    public void updateCurrentUserInformationTest_whenConfirmPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("passpass");
        updateProfileCurrentUser.setConfirmPassword("         ");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "confirmPassword", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenConfirmPassWordNotMatchWithPassword_thenThrowsPasswordNotMatcherException() throws Exception {
        //GIVEN
        UpdateProfile updateProfileCurrentUser = new UpdateProfile();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("password");
        updateProfileCurrentUser.setConfirmPassword("passwordnotmatch");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateProfile", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateProfile", "confirmPassword", "ConfirmPasswordNotMatch"))
                .andDo(print());
    }
}
