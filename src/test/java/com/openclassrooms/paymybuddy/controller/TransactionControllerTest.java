package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.security.MyUserDetailsService;
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

/**
 * Class of test for {@link TransactionController}
 *
 * @author Christine Duarte
 */
@WebMvcTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {
    /**
     * An instance of {@link MockMvc} that permit simulate a request HTTP
     */
    @Autowired
    private MockMvc mockMvcTransaction;

    /**
     * A mock of {@link ITransactionRepository}
     */
    @MockBean
    private ITransactionRepository transactionRepositoryMock;

    /**
     * A mock of {@link TransactionService}
     */
    @MockBean
    private TransactionService transactionServiceMock;

    /**
     * A mock of {@link UserService}
     */
    @MockBean
    private UserService userService;

    /**
     * A mock of {@link MyUserDetailsService}
     */
    @MockBean
    MyUserDetailsService myUserDetailsServiceMock;

    /**
     * A instance of {@link Pageable}
     */
    private Pageable pageable;

    /**
     * A List of  {@link FriendList}
     */
    private List<FriendList> friendListTest;

    /**
     * An instance of {@link PageImpl}
     */
    private PageImpl<DisplayingTransaction> displayingTransactionsPage;

    /**
     * method that create mocks to perform each tests
     */
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


        List<DisplayingTransaction> displayingTransactionsList = new ArrayList<>();
        displayingTransactionsList.add(new DisplayingTransaction("Lisette", "books", 15.0));
        displayingTransactionsList.add(new DisplayingTransaction("Wiliam", "dinner", 10.0));
        displayingTransactionsList.add(new DisplayingTransaction("Lisette", "tickets movies", 20.0));

        displayingTransactionsPage = new PageImpl<>(displayingTransactionsList);
    }

    /*-------------------------------------------------------------------------------------------------------
                                         Tests View transaction
    ---------------------------------------------------------------------------------------------------------*/

    /**
     * Method that test  get view transaction when the url is correct "/transaction"
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getTransactionsTransactionView_whenUrlIsSlashTransactionAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
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

    /**
     * Method that test get view transaction when the url is wrong "/transac"
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getTransactionsTransactionView_whenUrlTransIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/transac"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Method that test get transaction for the current user dada@email.fr
     * then return a Page of transactions
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
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

    /**
     * Method that test adding a  transaction when balance is enough
     * then return the Transaction added
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransactionTest_whenBalanceIsEnough_thenReturnTransactionAdded() throws Exception {
        //GIVEN
        String receiverEmail = "fifi@email.com";
        String emitterEmail = "dada@email.fr";
        User userEmitter = User.builder()
                .email(emitterEmail)
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

    /**
     * Method that test adding transaction when balance is insufficient
     * then throw a BalanceInsufficientException
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransactionTest_whenBalanceIsInsufficient_thenThrowBalanceInsufficientException() throws Exception {
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
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    /**
     * Method that test adding transaction when amount is null
     * then return fields error not null
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
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
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "NotNull"))
                .andDo(print());
    }

    /**
     * Method that test adding transaction when amount is greater than 1000
     * then return fields error max
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
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
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Max"))
                .andDo(print());
    }

    /**
     * Method that test adding transaction when amount is less than 1
     * then return fields error min
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
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
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Min"))
                .andDo(print());
    }

    /**
     * Method that test adding transaction when value of the selector friends email is empty
     * then return fields error notBlank
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
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
                .andExpect(model().attributeExists("sendTransaction", "transactions", "friendLists", "totalPagesTransaction", "currentPage"))
                .andExpect(model().attribute("totalPagesTransaction", is(1)))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("sendTransaction", "receiverEmail"))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }
}
