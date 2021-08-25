package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.security.MyUserDetailsService;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvcTransaction;

    @MockBean
    private ITransactionRepository transactionRepositoryMock;

    @MockBean
    private TransactionService transactionServiceMock;

    @MockBean
    private UserService userService;

    @MockBean
    MyUserDetailsService myUserDetailsService;

    private Pageable pageable;

    private List<FriendList> friendListTest;

    private PageImpl<DisplayingTransaction> displayingTransactionsPage;

    @BeforeEach
    public void setupPerTest() {
        pageable = PageRequest.of(0, 5);

        friendListTest = new ArrayList<>();
        friendListTest.add(new FriendList("kikine@email.fr", "Christine", "Duhamel"));
        friendListTest.add(new FriendList("wiwi@email.fr", "Wiliam", "Desouza"));
        friendListTest.add(new FriendList("baltazar@email.fr", "Baltazar", "Delobel"));
        friendListTest.add(new FriendList("barnabé@email.fr", "Barnabé", "Vincent"));
        friendListTest.add(new FriendList("eve@email.fr", "Eva", "Bernard"));
        friendListTest.add(new FriendList("marion@email.fr", "Marion", "Dubois"));

//        displayingFriendsPage = new PageImpl<>(friendListPageTest);

        List<DisplayingTransaction> displayingTransactionsList = new ArrayList<>();
        displayingTransactionsList.add(new DisplayingTransaction("Lisette", "books", 15.0));
        displayingTransactionsList.add(new DisplayingTransaction("Wiliam", "dinner", 10.0));
        displayingTransactionsList.add(new DisplayingTransaction("Lisette", "tickets movies", 20.0));

        displayingTransactionsPage = new PageImpl<>(displayingTransactionsList);
    }

    /*-------------------------------------------------------------------------------------------------------
                                         Tests View transaction
    ---------------------------------------------------------------------------------------------------------*/
    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsTransactionView_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/transaction")
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().size(6))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("transactions", Matchers.hasProperty("totalPages", is(1))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("firstName", is("Lisette")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("amount", is(20.0)))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Wiliam")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("wiwi@email.fr")))))
                .andExpect(model().attribute("currentPage", is(1)))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsTransactionView_whenUrlIsTransactionAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/transaction")
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().size(6))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsTransactionView_whenUrlHomeIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/home"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsTransactionView_whenCurrentUserIsDada_thenReturnTransactionsOfDada() throws Exception {
        //GIVEN
        List<DisplayingTransaction> displayingTransactions = new ArrayList<>();
        DisplayingTransaction displayingTransaction1 = new DisplayingTransaction("Lisette", "shopping  casa china", -15.0);
        DisplayingTransaction displayingTransaction2 = new DisplayingTransaction("Lisette", "movies tickets", 18.0);

        displayingTransactions.add(displayingTransaction1);
        displayingTransactions.add(displayingTransaction2);

        displayingTransactionsPage = new PageImpl<>(displayingTransactions);

        //WHEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.get("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("receiverEmail", "dada@email.fr")
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("firstName", is("Lisette")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("amount", is(-15.0)))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("description", is("shopping  casa china")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("amount", is(18.0)))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("description", is("movies tickets")))))
                .andDo(print());

    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenBalanceIsEnough_thenReturnTransactionAdded() throws Exception {
        //GIVEN
        String receiverEmail = "fifi@email.com";
        String emitterEmail = SecurityUtilities.currentUser;

        User userEmitter = User.builder()
                .email(SecurityUtilities.currentUser)
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(100.50)
                .accountBank(170974).build();

        User userReceiver = User.builder()
                .email("lise@email.fr")
                .password("monTropToppassword")
                .firstName("lisette")
                .lastName("Duhamel")
                .balance(10.0)
                .accountBank(170974).build();


        Transaction transactionTest = new Transaction();
        transactionTest.setTransactionId(1);
        transactionTest.setUserReceiver(userReceiver);
        transactionTest.setUserEmitter(userEmitter);
        transactionTest.setDescription("cinema");
        transactionTest.setAmount(16.0);
        transactionTest.setFees(0.08);

        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionRepositoryMock.save(isA(Transaction.class))).thenReturn(transactionTest);
        when(transactionServiceMock.addTransaction(isA(SendTransaction.class))).thenReturn(transactionTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("userEmitter", String.valueOf(userEmitter))
                        .param("receiverEmail", receiverEmail)
                        .param("amount", String.valueOf(transactionTest.getAmount()))
                        .param("description", transactionTest.getDescription())
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("sendTransaction", hasProperty("receiverEmail", is("fifi@email.com"))))
                .andExpect(model().attribute("sendTransaction", hasProperty("amount", is(16.0))))
                .andDo(print());
    }


    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenBalanceIsInsufficient_thenReturnBalanceInsufficientException() throws Exception {
        //GIVEN
        String receiverEmail = "luluM@email.fr";
        String emitterEmail = "dada@email.fr";

        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.addTransaction(isA(SendTransaction.class))).thenThrow(new BalanceInsufficientException("Insufficient account balance, your balance is: " + emitterEmail));
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("balance", "20")
                        .param("userEmitter", emitterEmail)
                        .param("receiverEmail", receiverEmail)
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "NotNull"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Max"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Min"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        when(userService.getFriendListByCurrentUserEmail()).thenReturn(friendListTest);
        when(transactionServiceMock.getCurrentUserTransactionsByEmail(isA(Pageable.class))).thenReturn(displayingTransactionsPage);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists","totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("sendTransaction", "receiverEmail"))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

}
