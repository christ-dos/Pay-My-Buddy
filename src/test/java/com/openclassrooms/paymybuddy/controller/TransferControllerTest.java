package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import com.openclassrooms.paymybuddy.security.MyUserDetailsService;
import com.openclassrooms.paymybuddy.service.TransferService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class of test for {@link TransferController}
 *
 * @author Christine Duarte
 */
@WebMvcTest(TransferController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransferControllerTest {
    /**
     * An instance of {@link MockMvc} that permit simulate a request HTTP
     */
    @Autowired
    private MockMvc mockMvcTransfer;

    /**
     * A mock of {@link TransferService}
     */
    @MockBean
    private TransferService transferServiceMock;

    /**
     * A mock of {@link MyUserDetailsService}
     */
    @MockBean
    private MyUserDetailsService myUserDetailsServiceMock;

    /**
     * An instance of {@link PageImpl}
     */
    private Page<DisplayingTransfer> displayingTransferPage;

    /**
     * method that create mocks to perform each tests
     */
    @BeforeEach
    public void setPertest() {
        List<DisplayingTransfer> displayingTransferList = new ArrayList<>();

        DisplayingTransfer displayingTransferCredit = new DisplayingTransfer();
        displayingTransferCredit.setTransferType(TransferTypeEnum.CREDIT);
        displayingTransferCredit.setAmount(50.0);
        displayingTransferCredit.setPostTradeBalance(150.0);
        displayingTransferCredit.setDescription("transfer PayMyBuddy");

        DisplayingTransfer displayingTransferDebitBalanceEnough = new DisplayingTransfer();
        displayingTransferDebitBalanceEnough.setTransferType(TransferTypeEnum.DEBIT);
        displayingTransferDebitBalanceEnough.setAmount(-80.0);
        displayingTransferDebitBalanceEnough.setPostTradeBalance(20.0);
        displayingTransferDebitBalanceEnough.setDescription("transfer BNP");

        displayingTransferList.add(displayingTransferCredit);
        displayingTransferList.add(displayingTransferDebitBalanceEnough);

        displayingTransferPage = new PageImpl<>(displayingTransferList);
    }
    /*-------------------------------------------------------------------------------------------------------
                                            Tests View transfer
       ---------------------------------------------------------------------------------------------------------*/
    /**
     * Method that test get view transfer when the url is correct "/transfer"
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getCurrentUserTransfersTest_whenUrlIsSlashTransfer_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().size(6))
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes", "totalPages", "currentPage"))
                .andDo(print());
    }

    /**
     * Method that test get view transfer when the url is wrong "/trans"
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getCurrentUserTransfersTest_whenUrlIsWrong_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(get("/trans"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Method that test get transfers of the current user when user is "dada@email.fr"
     * then return the list of transfer of "dada@email.fr"
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getCurrentUserTransfersTest_whenCurrentUserIsDada_thenReturnListTransferOfDada() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.get("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes", "totalPages", "currentPage"))
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("transfers", Matchers.hasProperty("totalElements", equalTo(2L))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("transferType", is(TransferTypeEnum.CREDIT)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(50.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(150.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer BNP")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("transferType", is(TransferTypeEnum.DEBIT)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer PayMyBuddy")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(-80.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(20.0)))))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when transfer type is CREDIT
     * then return the balance with the amount credited
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsCredit_thenReturnBalanceCreditedWithAmount() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", "dada@email.fr")
                        .param("password", "passpass")
                        .param("amount", String.valueOf(50.0))
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(5))
                        .param("transferType", String.valueOf(TransferTypeEnum.CREDIT))
                        .param("postTradeBalance", String.valueOf(120)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes", "totalPages", "currentPage"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(50.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.CREDIT))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("postTradeBalance", is(120.0))))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when transfer type is DEBIT and balance is enough
     * then return the balance with the amount debited
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsEnough_thenReturnTransferAddedWithUserWithNewBalanceDebitedfromAmount() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", String.valueOf(80.0))
                        .param("transferType", TransferTypeEnum.DEBIT.name())
                        .param("balance", String.valueOf(100.0))
                        .param("postTradeBalance", String.valueOf(180))
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(5))
                        .param("currentUser", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(80.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.DEBIT))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("postTradeBalance", is(180.0))))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when transfer type is DEBIT and balance Insufficient
     * then throw BalanceInsufficientException
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsInsufficient_thenThrowBalanceInsufficientException() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        when(transferServiceMock.addTransfer(isA(DisplayingTransfer.class))).thenThrow(new BalanceInsufficientException("Balance insufficient for execute the transfer"));
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", String.valueOf(180.0))
                        .param("transferType", TransferTypeEnum.DEBIT.name())
                        .param("balance", String.valueOf(100.0))
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1))).andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(180.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.DEBIT))))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "BalanceInsufficient"))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when amount is null
     * then return field error NotNull
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("transferType", TransferTypeEnum.CREDIT.name()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "NotNull"))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when amount is less to 1
     * then return field error Min
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "0.0")
                        .param("transferType", TransferTypeEnum.CREDIT.name()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "Min"))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when value selector of transfer type is blank
     * then return field error NotBlank
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenValueSelectorTypeIsBlank_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "20")
                        .param("transferType", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "transferType", "NotNull"))
                .andDo(print());
    }

    /**
     * Method that test adding transfer when value selector of transfer type is blank and amount is null
     * then return field error NotBlank and NotNull
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void addTransferCurrentUserTest_whenValueSelectorTypeIsBlankAndAmountIsNull_thenReturnFieldsErrorsNotBlankAndNotNull() throws Exception {
        //GIVEN
        when(transferServiceMock.getCurrentUserTransfers(isA(Pageable.class))).thenReturn(displayingTransferPage);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("transferType", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "NotNull"))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "transferType", "NotNull"))
                .andDo(print());
    }
}
