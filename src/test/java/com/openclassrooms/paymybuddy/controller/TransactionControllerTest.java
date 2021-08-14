package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    /*-------------------------------------------------------------------------------------------------------
                                         Tests View index
    ---------------------------------------------------------------------------------------------------------*/
    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsIndexView_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsIndexView_whenUrlIsIndexAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsIndexView_whenUrlHomeIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(get("/home"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void getTransactionsIndexView_whenCurrentUserIsDada_thenReturnTransactionsOfDada() throws Exception {
        //GIVEN


        List<DisplayingTransaction> transactions = new ArrayList<>();
        DisplayingTransaction displayingTransaction1 = new DisplayingTransaction("Lisette", "shopping  casa china", -15.0);
        DisplayingTransaction displayingTransaction2 = new DisplayingTransaction("Lisette", "movies tickets", 18.0);

        transactions.add(displayingTransaction1);
        transactions.add(displayingTransaction2);

        //WHEN
        when(transactionServiceMock.getCurrentUserTransactionsByEmail()).thenReturn(transactions);
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.get("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("receiverEmail", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
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
        String emitterEmail = "dada@email.fr";

        Transaction transactionTest = new Transaction();
        transactionTest.setTransactionId(1);
        transactionTest.setDescription("cinema");
        transactionTest.setAmount(16.0);
        transactionTest.setFees(0.08);

        when(transactionRepositoryMock.save(isA(Transaction.class))).thenReturn(transactionTest);
        when(transactionServiceMock.addTransaction(isA(Transaction.class))).thenReturn(transactionTest);
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("userEmail", emitterEmail)
                        .param("receiverEmail", receiverEmail)
                        .param("amount", String.valueOf(transactionTest.getAmount()))
                        .param("description", transactionTest.getDescription())
                        .param("fees", String.valueOf(0.08))).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attribute("transaction", hasProperty("receiverEmail", is("fifi@email.com"))))
                .andExpect(model().attribute("transaction", hasProperty("emitterEmail", is("dada@email.fr"))))
                .andExpect(model().attribute("transaction", hasProperty("fees", is(0.08))))
                .andExpect(model().attribute("transaction", hasProperty("amount", is(16.0))))
                .andDo(print());
    }


    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenBalanceIsInsufficient_thenReturnBalanceInsufficientException() throws Exception {
        //GIVEN
        String receiverEmail = "luluM@email.fr";
        String emitterEmail = "dada@email.fr";

        when(transactionServiceMock.addTransaction(isA(Transaction.class))).thenThrow(new BalanceInsufficientException("Insufficient account balance, your balance is: " + emitterEmail));
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("balance", "20")
                        .param("emitterEmail", emitterEmail)
                        .param("receiverEmail", receiverEmail)
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @WithMockUser(value = "spring")
    @Test
    public void addTransactionTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
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
    public void addTransactionTest_whenValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransaction.perform(MockMvcRequestBuilders.post("/index").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("transaction", "receiverEmail"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

}
