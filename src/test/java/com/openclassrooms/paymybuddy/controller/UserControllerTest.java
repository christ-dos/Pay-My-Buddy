package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.configuration.MyUserDetails;
import com.openclassrooms.paymybuddy.exception.EmailNotMatcherException;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.when;
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

    @MockBean
    private ITransactionService transactionServiceMock;

    @MockBean
    private IUserRepository userRepositoryMock;

    private Pageable pageable;

    private Page<FriendList> displayingFriendsPage;


    @BeforeEach
    public void setupPerTest() {
//        mockMvcUser = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
        pageable = PageRequest.of(0, 5);

        List<FriendList> friendListPageTest = new ArrayList<>();
        friendListPageTest.add(new FriendList("kikine@email.fr", "Christine", "Duhamel"));
        friendListPageTest.add(new FriendList("wiwi@email.fr", "Wiliam", "Desouza"));
        friendListPageTest.add(new FriendList("baltazar@email.fr", "Baltazar", "Delobel"));
        friendListPageTest.add(new FriendList("barnabé@email.fr", "Barnabé", "Vincent"));
        friendListPageTest.add(new FriendList("eve@email.fr", "Eva", "Bernard"));
        friendListPageTest.add(new FriendList("marion@email.fr", "Marion", "Dubois"));
        displayingFriendsPage = new PageImpl<>(friendListPageTest);

    }
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
//        MyUserDetails myUserDetails = new MyUserDetails("", "pass");
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
                                     Tests View home
     ------------------------------------------------------------------------------------------------------*/
    @Test
    public void getUserInformationHomeViewTest_whenCurrentUserIsDada_thenReturnFirstNameDamienAndLastNameSanchez() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("passpass")
                .balance(100.0)
                .build();

        List<FriendList> friendLists = new ArrayList<>();
        FriendList friendList = new FriendList(
                "lili@email.fr", "Elisabeth", "Duhamel");
        friendLists.add(friendList);

        List<DisplayingTransaction> displayingTransactions = new ArrayList<>();
        DisplayingTransaction displayingTransaction = new DisplayingTransaction(
                "Elisabeth", "books", 5.0);
        displayingTransactions.add(displayingTransaction);

        Page<FriendList> friendListPage = new PageImpl<>(friendLists);
        Page<DisplayingTransaction> displayingTransactionsPage = new PageImpl<>(displayingTransactions);

        when(userServiceMock.getFriendListByCurrentUserEmail()).thenReturn(friendLists);
        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcUser.perform(get("/home")
                        .param("user", String.valueOf(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user", "lastBuddy", "lastTransaction"))
                .andExpect(model().attribute("user", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("user", hasProperty("balance", is(100.0))))
                .andExpect(model().attribute("user", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("user", hasProperty("lastName", is("Sanchez"))))
                .andExpect(model().attribute("lastBuddy", hasProperty("firstName", is("Elisabeth"))))
                .andExpect(model().attribute("lastBuddy", hasProperty("lastName", is("Duhamel"))))
                .andExpect(model().attribute("lastTransaction", hasProperty("firstName", is("Elisabeth"))))
                .andExpect(model().attribute("lastTransaction", hasProperty("amount", is(5.0))))
                .andDo(print());

    }

    @Test
    public void getUserInformationHomeViewTest_whenListFriendOrListTransactionIsEmpty_thenDisplayingHomeViewNoneResult() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("passpass")
                .balance(100.0)
                .build();

        List<FriendList> friendLists = new ArrayList<>();
        friendLists.add(null);

        List<DisplayingTransaction> displayingTransactions = new ArrayList<>();
        displayingTransactions.add(null);

        Page<FriendList> friendListPage = new PageImpl<>(friendLists);
        Page<DisplayingTransaction> displayingTransactionPage = new PageImpl<>(displayingTransactions);

        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(friendListPage);
        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionPage);
        //WHEN
        //THEN
        mockMvcUser.perform(get("/home")
                        .param("user", String.valueOf(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("user", hasProperty("balance", is(100.0))))
                .andExpect(model().attribute("user", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("user", hasProperty("lastName", is("Sanchez"))))
                .andExpect(model().attribute("lastBuddy", nullValue()))
                .andExpect(model().attribute("lastTransaction", nullValue()))
                .andDo(print());
    }

    /*-----------------------------------------------------------------------------------------------------
                                        Tests View addfriend
     ------------------------------------------------------------------------------------------------------*/

    @WithMockUser(value = "spring")
    @Test
    public void getListConnectionsTest_whenUrlIsAddFriendAndGood_thenReturnStatusOK() throws Exception {
        //GIVEN
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend")
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "totalPages", "currentPage"))
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("friendLists", Matchers.isA(Page.class)))
                .andExpect(model().attribute("friendLists", Matchers.hasProperty("totalElements", equalTo(6L))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Christine")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Desouza")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("marion@email.fr")))))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getListConnectionsTest_whenUrlIsAddFriendAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
        //WHEN
        //THEN
        mockMvcUser.perform(get("/addfriend"))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("friendLists", "friendList"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenUserExistInDBAndNotExistInListFriend_thenWeCanAddToFriendInList() throws Exception {
        //GIVEN
        String friendEmailNotExistInList = "fifi@email.com";

        User userToAdd = User.builder()
                .email("fifi@email.com").firstName("Filipe").lastName("Dupont").build();
        when(userServiceMock.getUserByEmail(friendEmailNotExistInList)).thenReturn(userToAdd);
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
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
    }

    @WithMockUser(value = "spring")
    @Test
    public void addFriendToListConnectionTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "françois@email.fr";
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
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
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserAlreadyExist"))
                .andDo(print());
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
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
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

    @Test
    public void addFriendToListConnectionTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBlank() throws Exception {
        //GIVEN
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);

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

    @Test
    public void addFriendToListConnectionTest_whenEmailToAddAndEmailCurrentUserIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        when(userServiceMock.getFriendListByCurrentUserEmailPaged(isA(Pageable.class))).thenReturn(displayingFriendsPage);
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
                .andExpect(model().size(2))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("currentUser", "updateCurrentUser"))
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
    public void updateCurrentUserInformationTest_whenCurrentUserIsDada_thenReturnUserDadaUpdated() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", SecurityUtilities.userEmail)
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser", "message"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("lastName", is("Sanchez"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("password", is("passpasspass"))))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenFirstNameIsBlank_thenReturnFieldErrorNotBlankInFieldFirstName() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "firstName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenLastNameIsBlank_thenReturnFieldErrorNotBlankInFieldLastName() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "lastName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsLess8_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("pass");
        updateProfileCurrentUser.setConfirmPassword("pass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIsDadaAndPassWordIsGreaterThan30_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspasspasspasspasspasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspasspasspasspasspasspass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("        ");
        updateProfileCurrentUser.setConfirmPassword("passpass");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_whenConfirmPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("passpass");
        updateProfileCurrentUser.setConfirmPassword("         ");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void updateCurrentUserInformationTest_ConfirmPassWordNotMatchWithPassword_thenThrowsPasswordNotMatcherException() throws Exception {
        //GIVEN
        User currentUser = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("pass")
                .build();

        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("password");
        updateProfileCurrentUser.setConfirmPassword("passwordnotmatch");

        when(userServiceMock.getUserByEmail(isA(String.class))).thenReturn(currentUser);
        when(userServiceMock.updateProfile(isA(UpdateCurrentUser.class))).thenThrow(new PasswordNotMatcherException("confirmPassword not match with password"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmEmail", updateProfileCurrentUser.getEmail())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "ConfirmPasswordNotMatch"))
                .andDo(print());
    }
    /*-----------------------------------------------------------------------------------------------------
                                     Tests View signup
  ------------------------------------------------------------------------------------------------------*/
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBAndEmailMatchConfirmEmailAndPasswordMatchConfirmPassword_thenReturnUserAddedAndMessageSuccess() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("addUser", "message"))
                .andExpect(model().attribute("message", is("Account registered with success!")))
                .andExpect(model().attribute("addUser", hasProperty("firstName", is("Ines"))))
                .andExpect(model().attribute("addUser", hasProperty("lastName", is("Martin"))))
                .andExpect(model().attribute("addUser", hasProperty("email", is("inim@email.fr"))))
                .andExpect(model().attribute("addUser", hasProperty("confirmEmail", is("inim@email.fr"))))
                .andExpect(model().attribute("addUser", hasProperty("password", is("passpass"))))
                .andExpect(model().attribute("addUser", hasProperty("confirmPassword", is("passpass"))))
                .andDo(print());
    }

         //******************************Tests Errors in fields*************************************
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldFirstNameIsBlank_thenReturnErrorInFieldsFirstName() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr"
                , 123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "firstName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldLastNameIsBlank_thenReturnErrorInFieldsLastName() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "lastName", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldEmailIsBlank_thenReturnErrorInFieldsEmail() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "email", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldConfirmEmailIsBlank_thenReturnErrorInFieldsConfirmEmail() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmEmail", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsBlank_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "        ", "passpass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsLess8_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "pass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "Size"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsGreaterThan30_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpasspasspasspasspasspasspass", "passpass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "Size"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsBlank_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "        ", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsLess8_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "pass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsGreaterThan30_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpasspasspasspasspasspasspass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButAccountBankIsNull_thenReturnErrorInFieldsAccountBank() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                null);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "accountBank", "NotNull"))
                .andDo(print());
    }
//************************************************Test Exceptions view signup**********************************************
    @Test
    public void signUpUserViewSignUp_whenUserAlreadyExistInDB_thenThrowsUserAlreadyExistException() throws Exception {
        //GIVEN
        User userAlreadyExist = User.builder()
                .email("dada@email.fr")
                .firstName("Damien")
                .lastName("Sanchez")
                .password("passpass")
                .build();
        AddUser addUser = new AddUser(
                "Damien", "Sanchez", "passpass", "passpass", "dada@email.fr", "dada@email.fr",
                123456);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userAlreadyExist);
        when(userServiceMock.addUser(isA(AddUser.class))).thenThrow(new UserAlreadyExistException("This email already exist"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("addUser", "message"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "email", "EmailAlreadyExist"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButEmailAndConfirmEmailNotMatch_thenThrowsUserEmailNotMatcherException() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Albert", "Masarin", "passpass", "passpass", "albert@email.fr", "emailNotMatche@email.fr",
                0123654);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        when(userServiceMock.addUser(isA(AddUser.class))).thenThrow(new EmailNotMatcherException("Field confirm email not match with email"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmEmail())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("addUser", "message"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmEmail", "ConfirmEmailNotMatcher"))
                .andDo(print());
    }

    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordAndConfirmPasswordNotMatch_thenThrowsUserPasswordNotMatcherException() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Albert", "Masarin", "passpass", "notmatch", "albert@email.fr", "albert@email.fr",
                0123654);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        when(userServiceMock.addUser(isA(AddUser.class))).thenThrow(new PasswordNotMatcherException("Field confirm password not match with password"));
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmEmail())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("addUser", "message"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "ConfirmPasswordNotMatcher"))
                .andDo(print());
    }
}


