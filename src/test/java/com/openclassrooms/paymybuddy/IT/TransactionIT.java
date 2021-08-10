package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.model.Transaction;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TransactionIT {

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

    /*--------------------------------------------------------------------------------------------------
                                   Integration tests View index
   ------------------------------------------------------------------------------------------------------
         -----------------------------------------------------------------------------------------
                                     Tests in view index in  URL /
        ------------------------------------------------------------------------------------------*/
    @Test
    public void showIndexViewTest_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @Test
    public void showIndexViewTest_whenUrlHomeIsWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/home")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenURLIsSlashAndBalanceIsEnough_thenWeCanAddTransaction() throws Exception {
        //GIVEN
        Transaction transaction = Transaction.builder()
                .emitterEmail("dada@email.fr").receiverEmail("luluM@email.fr")
                .description("Movies tickets").amount(2.0)
                .build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenURLIsSlashAndBalanceIsInsufficient_thenReturnFieldErrorBalanceInsufficientException() throws Exception {
        //GIVEN
        Transaction transaction = new Transaction();
        transaction.setEmitterEmail("dada@email.fr");
        transaction.setReceiverEmail("luluM@email.fr");
        transaction.setDescription("Movies tickets");
        transaction.setAmount(30.0);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }


    @Test
    public void addTransactionTest_whenUrlIsSlashAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsSlashAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUlrIsSlashAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Min"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsSlashAndValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }
    /*--------------------------------------------------------------------------------------------------------
                                        Tests in  View index and URL /index
    -----------------------------------------------------------------------------------------------------------*/

    @Test
    public void addTransactionTest_whenUrlIsIndexAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenURLIsIndexAndBalanceIsEnough_thenWeCanAddTransaction() throws Exception {
        //GIVEN
        Transaction transaction = Transaction.builder()
                .emitterEmail("dada@email.fr").receiverEmail("luluM@email.fr")
                .description("Movies tickets").amount(10.0).fees(0.05)
                .build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attribute("transaction",hasProperty("fees", is(0.05))))
                .andExpect(model().attribute("transaction",hasProperty("amount", is(10.0))))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenURLIsIndexBalanceIsInsufficient_thenReturnFieldErrorBalanceInsufficientException() throws Exception {
        //GIVEN
        Transaction transaction = Transaction.builder()
                .emitterEmail("dada@email.fr").receiverEmail("luluM@email.fr")
                .description("Movies tickets").amount(30.0)
                .build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsIndexAndAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsIndexAndAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsIndexAndAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Min"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenUrlIsIndexAndValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

}
