package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.configuration.MyUserDetails;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
//@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserService userServiceMock;

    @MockBean
    private IUserRepository userRepositoryMock;

    @MockBean
    private ITransactionRepository transactionRepositoryMock;

    @MockBean
    private IFriendRepository friendRepositoryMock;

    @MockBean
    private TransactionService transactionServiceMock;

    @MockBean
    private UserDetailsService userDetailsServiceMock;

//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders
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
        mockMvc.perform(get("/customlogin"))
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
        mockMvc.perform(get("/log"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitLoginViewTest_whenUserNameIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        MyUserDetails myUserDetails = new MyUserDetails("", "pass");
        when(userDetailsServiceMock.loadUserByUsername(isA(String.class))).thenReturn(myUserDetails);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/customlogin")
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
        MyUserDetails myUserDetails = new MyUserDetails("dada@email.fr", "pass");
        when(userDetailsServiceMock.loadUserByUsername(isA(String.class))).thenReturn(myUserDetails);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()).param("username", "dada@email.fr").param("password", "pass")
                        .accept(MediaType.ALL)).andExpect(status().isOk())
//                .andExpect(redirectedUrl("/index"))
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
//                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsStringIgnoringCase("/index")))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userDetails"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }


    /*-------------------------------------------------------------------------------------------------------
                                         Tests View Index
    ---------------------------------------------------------------------------------------------------------*/
    @WithMockUser(value = "spring")
    @Test
    public void showIndexViewTest_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void showIndexViewTest_whenUrlIsIndexAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void showIndexViewTest_whenUrlHomeIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/home"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenBalanceIsEnough_thenReturnTransactionAdded() throws Exception {
        //GIVEN
        String receiverEmail = "fifi@email.com";
        String emitterEmail = "kikine@email.fr";

        Transaction transactionTest = new Transaction();
        transactionTest.setTransactionId(1);
        transactionTest.setDescription("cinema");
        transactionTest.setAmount(16.0);
        transactionTest.setFees(0.08);

        when(transactionRepositoryMock.save(transactionTest)).thenReturn(transactionTest);
        when(transactionServiceMock.addTransaction(transactionTest)).thenReturn(transactionTest);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("userEmail", emitterEmail)
                        .param("receiverEmail", receiverEmail)
                        .param("amount", String.valueOf(transactionTest.getAmount()))
                        .param("description", transactionTest.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenBalanceIsInsufficient_thenReturnBalanceInsufficientException() throws Exception {
        //GIVEN
        String receiverEmail = "luluM@email.fr";
        String emitterEmail = "dada@email.fr";

        when(transactionServiceMock.addTransaction(isA(Transaction.class))).thenThrow(new BalanceInsufficientException("Insufficient account balance, your balance is: " + emitterEmail));
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("balance", "20")
                        .param("emitterEmail", emitterEmail)
                        .param("receiverEmail", receiverEmail)
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("transaction", "amount"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("transaction", "amount"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Min"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitIndexViewTest_whenValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("transaction", "receiverEmail"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

    /*-----------------------------------------------------------------------------------------------------
                                        Tests View addfriend
     ------------------------------------------------------------------------------------------------------*/
    @WithMockUser(value = "spring")
    @Test
    public void showAddFriendViewTest_whenUrlIsAddfriendAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/addfriend"))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("friendLists", "friendList"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void showAddFriendViewTest_whenUrlIsAddAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/add"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitAddFriendTest_whenUserExistInDBAndNotExistInListFriend_thenWeCanaddToFriendInList() throws Exception {
        //GIVEN
        String friendEmailNotExistInList = "fifi@email.com";

        User userToAdd = User.builder()
                .email("fifi@email.com").firstName("Filipe").lastName("Dupont").build();
        when(userServiceMock.getUserByEmail(friendEmailNotExistInList)).thenReturn(userToAdd);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
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
    public void submitAddFriendTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "françois@email.fr";
        String userEmail = "dada@email.fr";

        User userFrancois = User.builder()
                .email("françois@email.fr")
                .password("monTropToppassword")
                .firstName("François")
                .lastName("Dujardin")
                .balance(30.50)
                .accountBank(170974).build();


        List<FriendList> friendListMock;
        friendListMock = new ArrayList<>();
        FriendList friend1 = new FriendList();
        FriendList friend2 = new FriendList();

        friend1.setEmail("françois@email.fr");
        friend1.setFirstName("François");
        friend1.setLastName("Dujardin");

        friend2.setEmail("amartin@email.fr");
        friend2.setFirstName("Albert");
        friend2.setLastName("Martin");

        friendListMock.add(friend1);
        friendListMock.add(friend2);

        when(userServiceMock.getUserByEmail(friendEmailAlreadyExist)).thenReturn(userFrancois);
        when(userServiceMock.getFriendListByEmail(userEmail)).thenReturn(friendListMock);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "françois@email.fr")
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserAlreadyExist"))
                .andDo(print());
        //verify that addFriendUser was not called because friend already exists in DB
        verify(userServiceMock, times(0)).addFriendUser(anyString(), anyString());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitAddFriendTest_whenFriendToAddedNotExistInDB_thenCanNotBeAddedErrorMessageInFieldEmailUserNotExist() throws Exception {
        //GIVEN
        String springToken = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        CsrfToken csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());

        String friendEmailNotExist = "wiwi@email.fr";
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", friendEmailNotExist).sessionAttr(springToken, csrfToken))

                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserNotExistDB"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitAddFriendTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "NotBlank"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void submitAddFriendTest_whenEmailToAddAndEmailUserIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UnableAddingOwnEmail"))
                .andDo(print());
    }
}


