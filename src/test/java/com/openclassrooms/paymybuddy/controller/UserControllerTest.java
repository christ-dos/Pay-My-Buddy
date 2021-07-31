package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.DTO.ReceivingDataTransactionView;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
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

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvcUser;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private IUserRepository userRepositoryMock;

    @MockBean
    private ITransactionRepository transactionRepositoryMock;

    @MockBean
    private TransactionService transactionServiceMock;

    //***********************Tests View Login*************************************
    @Test
    public void showLoginViewTest_whenUrlLoginIsGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                //.andExpect(model().size(3))
                // .andExpect(model().attributeExists("friendLists","transactions","receivingDataTransactionView"))
                .andDo(print());
    }

    //******************************Tests View Index****************************
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
    public void showIndexViewTest_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andDo(print());
    }

    @Test
    public void showIndexViewTest_whenUrlIsIndexAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andDo(print());
    }

    @Test
    public void showIndexViewTest_whenUrlHomeIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/home"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUserExistInDBAndFriendEmailTooAndBalanceIsEnough_thenWeCanAddTransaction() throws Exception {
        //GIVEN
        String friendEmail = "fifi@email.com";
        String userEmail = "kikine@email.fr";

        Transaction transactionTest = new Transaction();
        transactionTest.setTransactionId(1);
        transactionTest.setDescription("cinema");
        transactionTest.setAmount(16.0);

        ReceivingDataTransactionView receivingDataTransactionView = new ReceivingDataTransactionView();
        receivingDataTransactionView.setUserEmail("fifi@email.com");
        receivingDataTransactionView.setFriendEmail("kikine@email.fr");
        receivingDataTransactionView.setDescription("books");
        receivingDataTransactionView.setAmount(12.58);

        doNothing().when(transactionRepositoryMock).saveTransaction(userEmail, friendEmail, transactionTest.getAmount(), transactionTest.getDescription());
        doNothing().when(transactionServiceMock).addTransaction(userEmail, friendEmail, transactionTest.getAmount(), transactionTest.getDescription());
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("userEmail",userEmail)
                        .param("friendEmail", receivingDataTransactionView.getFriendEmail())
                        .param("amount", String.valueOf(receivingDataTransactionView.getAmount()))
                        .param("description", receivingDataTransactionView.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenAmountIsNull_thenReturnFieldsErrors() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "")
                        .param("friendEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("receivingDataTransactionView", "amount"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenAmountIsGreaterTo1000_thenReturnFieldsErrors() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "1500")
                        .param("friendEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("receivingDataTransactionView", "amount"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenAmountIsLessTo1_thenReturnFieldsErrors() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "0.5")
                        .param("friendEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("receivingDataTransactionView", "amount"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenValueSelectorFrienEmailIsEmpty_thenReturnFieldsErrors() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "2")
                        .param("friendEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("receivingDataTransactionView", "friendEmail"))
                .andDo(print());
    }

    //***********************Tests View Addfriend**********************************
    @Test
    public void showAddFriendViewTest_whenUrlIsAddfriendAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
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

    @Test
    public void showAddFriendViewTest_whenUrlIsAddAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/add"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenUserExistInDBAndNotExistInListFriend_thenWeCanaddToFriendInList() throws Exception {
        //GIVEN
        String friendEmailNotExistInList = "fifi@email.com";
        String userEmail = "kikine@email.fr";

        User userToAdd = User.builder()
                .email("fifi@email.com").firstName("Filipe").lastName("Dupont").build();

        doNothing().when(userRepositoryMock).saveFriend(userEmail, friendEmailNotExistInList);
        doNothing().when(userServiceMock).addFriendUser(userEmail, friendEmailNotExistInList);
        when(userServiceMock.getUserByEmail(friendEmailNotExistInList)).thenReturn(userToAdd);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .param("user", String.valueOf(userToAdd))
                        .param("email", userToAdd.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "sara@email.fr";
        String userEmail = "kikine@email.fr";

        Set<IFriendList> friendSetMock;
        FriendList friend1 = new FriendList();
        FriendList friend2 = new FriendList();
        friendSetMock = new HashSet<>();

        friend1.setEmail("sara@email.fr");
        friend1.setFirstName("François");
        friend1.setLastName("Dujardin");

        friend2.setEmail("amartin@email.fr");
        friend2.setFirstName("Albert");
        friend2.setLastName("Martin");

        friendSetMock.add(friend1);
        friendSetMock.add(friend2);

        doNothing().when(userRepositoryMock).saveFriend(userEmail, friendEmailAlreadyExist);
        doNothing().when(userServiceMock).addFriendUser(userEmail, friendEmailAlreadyExist);
        when(userServiceMock.getFriendListByEmail(friendEmailAlreadyExist)).thenReturn(friendSetMock);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .param("email", friendEmailAlreadyExist))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                //.andExpect(model().attribute("friendList",new FriendList("sara@email.fr",null,null)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andDo(print());
        //verify that addFriendUser was not called because friend already exists in DB
        verify(userServiceMock, times(0)).addFriendUser(anyString(), anyString());
    }

    @Test
    public void submitAddFriendTest_whenFriendToAddedNotExistInDB_thenCanNotBeAddedErrorMessageInFieldEmailUserNotExist() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        String userEmail = "kikine@email.fr";

        doNothing().when(userRepositoryMock).saveFriend(userEmail, friendEmailNotExist);
        doNothing().when(userServiceMock).addFriendUser(userEmail, friendEmailNotExist);
        when(userServiceMock.getUserByEmail(friendEmailNotExist)).thenReturn(null);
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
    public void submitAddFriendTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
        String friendEmailNotExist = "";
        String userEmail = "kikine@email.fr";
        doNothing().when(userRepositoryMock).saveFriend(userEmail, friendEmailNotExist);
        doNothing().when(userServiceMock).addFriendUser(userEmail, friendEmailNotExist);
        when(userServiceMock.getUserByEmail(friendEmailNotExist)).thenReturn(null);
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
}


