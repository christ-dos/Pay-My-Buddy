package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.DTO.ReceivingDataTransactionView;
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
//        String friendEmail = "luluM@email.com";
//        String userEmail = "dada@email.fr";
//        Double amount = 15.0;
//        String description = " Movies tickets";
        Double balance = 20.0;

        Transaction transactionTest = new Transaction();
        transactionTest.setTransactionId(1);
        transactionTest.setDescription("cinema");
        transactionTest.setAmount(16.0);

        ReceivingDataTransactionView receivingDataTransactionView = new ReceivingDataTransactionView();
        receivingDataTransactionView.setUserEmail("dada@email.fr");
        receivingDataTransactionView.setFriendEmail("luluM@email.com");
        receivingDataTransactionView.setDescription("Movies tickets");
        receivingDataTransactionView.setAmount(15.58);

        //WHEN
        //THEN
        mockMvcUser.perform(MockMvcRequestBuilders.post("/index")
                                .param("userEmail", receivingDataTransactionView.getUserEmail())
                                .param("receiverEmail", receivingDataTransactionView.getFriendEmail())
                                //.param("friendEmail", receivingDataTransactionView.getFriendEmail())
                                .param("amount", String.valueOf(receivingDataTransactionView.getAmount()))
                                .param("description", receivingDataTransactionView.getDescription())
                        //.param("balance", String.valueOf(balance)))

                ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("friendLists", "transactions", "receivingDataTransactionView"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }


    //**********************Integartion tests in view addfriend*****************
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
    public void saveFriendTest_whenUsersExistAndFriendIsNotInListFriend_thenReturnStatusIsOk() throws Exception {
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
    public void saveFriendTest_whenEmailFriendNotExistInDB_thenReturnErrorInFieldEmailUserNotExist() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
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
    public void saveFriendTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
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

    @Test
    public void saveFriendTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
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
//                .andExpect(model().attribute("friendList",new FriendList("sara@email.fr",null,null)))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors())
                .andDo(print());
    }


}
