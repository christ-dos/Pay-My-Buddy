package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserTestIT {

    @Autowired
    private MockMvc mockMvcUser;

    //************************Integration tests View index**********************
    //***************************Tests in  URL /***************************************
    @Test
    public void showIndexViewTest_whenUrlIsSlashAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/"))
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
        mockMvcUser.perform(get("/home"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenURLIsSlashAndBalanceIsEnough_thenWeCanAddTransaction() throws Exception {
        //GIVEN
        Double balance = 20.0;

        Transaction transaction = new Transaction();
        transaction.setEmitterEmail("dada@email.fr");
        transaction.setReceiverEmail("luluM@email.fr");
        transaction.setDescription("Movies tickets");
        transaction.setAmount(2.0);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription())

                ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenURLIsSlashAndBalanceIsInsufficient_thenReturnFieldErrorBalanceInsufficientException() throws Exception {
        //GIVEN
        Double balance = 20.0;

        Transaction transaction = new Transaction();
        transaction.setEmitterEmail("dada@email.fr");
        transaction.setReceiverEmail("luluM@email.fr");
        transaction.setDescription("Movies tickets");
        transaction.setAmount(30.0);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription())

                ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }


    @Test
    public void submitIndexViewTest_whenUrlIsSlashAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsSlashAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUlrIsSlashAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Min"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsSlashAndValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/")
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }
    //************************************Tests in View index in URL /index****************************************

    @Test
    public void showIndexViewTest_whenUrlIsIndexAndGood_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenURLIsIndexAndBalanceIsEnough_thenWeCanAddTransaction() throws Exception {
        //GIVEN
        Double balance = 20.0;

        Transaction transaction = new Transaction();
        transaction.setEmitterEmail("dada@email.fr");
        transaction.setReceiverEmail("luluM@email.fr");
        transaction.setDescription("Movies tickets");
        transaction.setAmount(5.0);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription())

                ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenURLIsIndexBalanceIsInsufficient_thenReturnFieldErrorBalanceInsufficientException() throws Exception {
        //GIVEN
        Double balance = 20.0;

        Transaction transaction = new Transaction();
        transaction.setEmitterEmail("dada@email.fr");
        transaction.setReceiverEmail("luluM@email.fr");
        transaction.setDescription("Movies tickets");
        transaction.setAmount(30.0);
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("userEmail", transaction.getEmitterEmail())
                        .param("receiverEmail", transaction.getReceiverEmail())
                        .param("amount", String.valueOf(transaction.getAmount()))
                        .param("description", transaction.getDescription())

                ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "BalanceInsufficientException"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsIndexAndAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsIndexAndAmountIsGreaterTo1000_thenReturnFieldsErrorsMax() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "1500")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Max"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsIndexAndAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "0.5")
                        .param("receiverEmail", "luluM@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "amount", "Min"))
                .andDo(print());
    }

    @Test
    public void submitIndexViewTest_whenUrlIsIndexAndValueSelectorFriendEmailIsEmpty_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                        .param("amount", "2")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendLists", "transactions", "transaction"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("transaction", "receiverEmail", "NotBlank"))
                .andDo(print());
    }

    //**********************Integration tests in view addfriend*****************
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
        mockMvcUser.perform(get("/addfriend")
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
        mockMvcUser.perform(get("/friend"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenUsersExistAndFriendIsNotInListFriend_thenReturnStatusIsOk() throws Exception {
        //GIVEN
        String userEmail = "tela@email.fr";
        User userFriendToAdd = User.builder()
                .email("lili@email.fr").firstName("Elisabeth").lastName("Dupont").build();
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .param("email", userFriendToAdd.getEmail())
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenEmailFriendNotExistInDB_thenReturnErrorInFieldEmailUserNotExistDB() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .param("email", friendEmailNotExist))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserNotExistDB"))
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void submitAddFriendTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "ggpassain@email.fr";
        String userEmail = "tela@email.fr";
        //WHEN

        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
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
    public void submitAddFriendTest_whenEmailToAddAndEmailUserIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/addfriend")
                        .param("email", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UnableAddingOwnEmail"))
                .andDo(print());
    }
}
