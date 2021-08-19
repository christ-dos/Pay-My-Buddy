package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                                   Integration tests View transaction
   ----------------------------------------------------------------------------------------------------*/

    @Test
    public void getTransactionsViewTransactionTest_whenUrlIsSlashtransferAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andDo(print());
    }

    @Test
    public void getTransactionsViewTransactionTest_whenIsAWrongUrlSlashIndex_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/index")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    public void getTransactionsViewTransactionTest_whenCurrentUserIsDada_thenReturnTransactionsOfDada() throws Exception {
        //GIVEN
//        List<DisplayingTransaction> transactions = new ArrayList<>();
//        DisplayingTransaction displayingTransaction1 = new DisplayingTransaction("Lisette", "shopping  casa china", -15.0);
//        DisplayingTransaction displayingTransaction2 = new DisplayingTransaction("Lisette", "movies tickets", 18.0);
//
//        transactions.add(displayingTransaction1);
//        transactions.add(displayingTransaction2);

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("receiverEmail", "dada@email.fr"))
//                        .param("transactions", String.valueOf(transactions)))

                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("firstName", is("Lubin")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("amount", is(-15.0)))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("description", is("books")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("firstName", is("Geraldine")))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("amount", is(-5.0)))))
                .andExpect(model().attribute("transactions", hasItem(hasProperty("description", is("diner")))))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenBalanceIsEnough_thenReturnTransactionAdded() throws Exception {
        //GIVEN
        Transaction transaction = Transaction.builder()
                .userEmitter(User.builder()
                        .email("dada@email.fr").password("passpasspass").build())
                        .description("sweet").amount(10.0)
                        .userReceiver(User.builder()
                                .email("ggpassain@email.fr").
                                password("passpasspass").build()
                )
                .build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("receiverEmail", transaction.getUserReceiver().getEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("password", transaction.getUserEmitter().getPassword(),transaction.getUserReceiver().getPassword())
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("sendTransaction", hasProperty("receiverEmail", is("ggpassain@email.fr"))))
                .andExpect(model().attribute("sendTransaction", hasProperty("amount", is(10.0))))
                .andExpect(model().attribute("sendTransaction", hasProperty("description", is("sweet")))).andDo(print());
    }

    @Test
    public void addTransactionTest_whenBalanceIsInsufficient_thenReturnFieldErrorBalanceInsufficientException() throws Exception {
        //GIVEN
        Transaction transaction = new Transaction();
        transaction.setUserEmitter(User.builder().email(SecurityUtilities.userEmail).build());
        transaction.setUserReceiver(User.builder().email("luluM@email.fr").build());
        transaction.setDescription("Movies tickets");
        transaction.setAmount(200.0);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("userEmail", transaction.getUserEmitter().getEmail())
                        .param("receiverEmail", transaction.getUserReceiver().getEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription()))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Max"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "amount", "Min"))
                .andDo(print());
    }

    @Test
    public void addTransactionTest_whenValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("amount", "2")
                        .param("userReceiver", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "sendTransaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("sendTransaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

}
