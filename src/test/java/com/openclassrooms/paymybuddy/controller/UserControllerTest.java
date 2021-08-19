package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.configuration.MyUserDetails;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
//@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvcUser;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private UserDetailsService userDetailsServiceMock;


//    @BeforeEach
//    public void setup() {
//        mockMvcUser = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }
 /*-----------------------------------------------------------------------------------------------------
                                     Tests View Login
 ------------------------------------------------------------------------------------------------------*/

    @Test
//    @WithMockUser
    public void showLoginViewTest_whenUrlLoginIsGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/customlogin"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userDetails"))
                .andDo(print());
    }

    @WithMockUser(value = "@dada@email.fr")
    @Test
    public void showLoginViewTest_whenUrlLogAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/log"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitLoginViewTest_whenUserNameIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        MyUserDetails myUserDetails = new MyUserDetails("", "pass");
//        when(userDetailsServiceMock.loadUserByUsername(isA(String.class))).thenReturn(myUserDetails);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/customlogin")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.ALL)).andExpect(status().isOk())
//                .andExpect(redirectedUrl("/customlogin"))
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/customlogin"))
//                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsStringIgnoringCase("/customlogin")))
                .andExpect(model().attributeExists("userDetails"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("userDetails", "username", "NotBlank"))
                .andDo(print());
    }

    @WithMockUser(username = "dada@email.fr", password = "pass")
    @Test
    public void submitLoginViewTest_whenUserExistAndPasswordIsGood_thenReturnStatusRedirectUrlIndex() throws Exception {
        //GIVEN
        String username = SecurityUtilities.userEmail;
        MyUserDetails myUserDetails = new MyUserDetails("dada@email.fr", "pass");
        when(userDetailsServiceMock.loadUserByUsername(username)).thenReturn(myUserDetails);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .with(SecurityMockMvcRequestPostProcessors.user(username).password("pass"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", username)
                        .param("password", "pass")
                        .accept(MediaType.ALL)).andExpect(status().isOk())
//                .andExpect(redirectedUrl("/index"))
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
//                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsStringIgnoringCase("/index")))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userDetails"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    /*-----------------------------------------------------------------------------------------------------
                                        Tests View addfriend
     ------------------------------------------------------------------------------------------------------*/

    @WithMockUser(value = "spring")
    @Test
    public void getListConnectionsTest_whenUrlIsAddFriendAndGood_thenReturnStatusOK() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getListConnectionsTest_whenUrlIsAddfriendAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend"))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("friendLists", "friendList"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenUserExistInDBAndNotExistInListFriend_thenWeCanaddToFriendInList() throws Exception {
        //GIVEN
        String friendEmailNotExistInList = "fifi@email.com";

        User userToAdd = User.builder()
                .email("fifi@email.com").firstName("Filipe").lastName("Dupont").build();
        when(userServiceMock.getUserByEmail(friendEmailNotExistInList)).thenReturn(userToAdd);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", userToAdd.getEmail())
                        .param("firstName", userToAdd.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
//        verify(friendRepositoryMock, times(1)).save(friendAdded);
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "françois@email.fr";

        when(userServiceMock.addFriendCurrentUserList(friendEmailAlreadyExist)).thenThrow(new UserAlreadyExistException("user already exist in list of connections"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "françois@email.fr")
                        .param("firstName", "François")
                        .param("lastName", "Dujardin")
                        .param("userEmail", SecurityUtilities.userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserAlreadyExist"))
                .andDo(print());
        verify(userServiceMock, times(1)).addFriendCurrentUserList(anyString());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenFriendToAddedNotExistInDB_thenCanNotBeAddedErrorMessageInFieldEmailUserNotExist() throws Exception {
        //GIVEN
//        String springToken = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
//        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
//        CsrfToken csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());

        String friendEmailNotExist = "wiwi@email.fr";
        when(userServiceMock.addFriendCurrentUserList(friendEmailNotExist)).thenThrow(new UserNotFoundException("User's email not exist"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("userEmail", SecurityUtilities.userEmail)
                        .param("email", friendEmailNotExist)//.sessionAttr(springToken, csrfToken))
                ).andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserNotExistDB"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "NotBlank"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenEmailToAddAndEmailCurrentUserIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", SecurityUtilities.userEmail))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UnableAddingOwnEmail"))
                .andDo(print());
    }

    /*-----------------------------------------------------------------------------------------------------
                                      Tests View profile
   ------------------------------------------------------------------------------------------------------*/
    @WithMockUser(value = "spring")
    @Test
    public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfileAndGood_thenReturnStatusOK() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();
        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(get("/profile")
                        .param("email", SecurityUtilities.userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attribute("currentUser", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("currentUser", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("currentUser", hasProperty("lastName", is("Sanchez"))))
                .andExpect(model().attribute("currentUser", hasProperty("password", is("pass"))))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/prof"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserFieldFirstNameIsBlank_thenReturnFieldErrorNotBlank() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email(SecurityUtilities.userEmail)
                .firstName("")
                .lastName("Duhamel")
                .password("passpass")
                .build();
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", currentUser.getEmail())
                        .param("firstName", currentUser.getFirstName())
                        .param("lastName", currentUser.getLastName())
                        .param("password", currentUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("user","currentUser"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIskikine_thenReturnUserkikineUpdated() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email(SecurityUtilities.userEmail)
                .firstName("Christine")
                .lastName("Duhamel")
                .password("passpass")
                .build();
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", currentUser.getEmail())
                        .param("firstName", currentUser.getFirstName())
                        .param("lastName", currentUser.getLastName())
                        .param("password", currentUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("user", hasProperty("firstName", is("Christine"))))
                .andExpect(model().attribute("user", hasProperty("lastName", is("Duhamel"))))
                .andExpect(model().attribute("user", hasProperty("password", is("passpass"))))
                .andDo(print());
    }


}


