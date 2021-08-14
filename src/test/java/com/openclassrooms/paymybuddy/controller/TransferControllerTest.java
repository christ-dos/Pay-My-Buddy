package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(TransferController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvcTransfer;

    @MockBean
    private TransferService transferServiceMock;

    @MockBean
    private ITransferRepository transferRepositoryMock;

    @MockBean
    private IUserRepository userRepositoryMock;

    /*-------------------------------------------------------------------------------------------------------
                                            Tests View transfer
       ---------------------------------------------------------------------------------------------------------*/
    @Test
    public void getCurrentUserTransfersTest_whenUrlIsSlashTransfer_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("displayingTransfer", "transfers"))
                .andDo(print());
    }

    @Test
    public void getCurrentUserTransfersTest_whenUrlIsWrong_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(get("/trans"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getCurrentUserTransfersTest_whenCurrentUserIsDada_thenReturnListTransferOfDada() throws Exception {
        //GIVEN
        List<DisplayingTransfer> displayingTransferList = new ArrayList<>();
        DisplayingTransfer displayingTransfer1 = new DisplayingTransfer(
                "transfer payMyBuddy", "credit", 100.0, 200.0);
        DisplayingTransfer displayingTransfer2 = new DisplayingTransfer("transfer Montepio", "debit", -50.0, 150.0);

        displayingTransferList.add(displayingTransfer1);
        displayingTransferList.add(displayingTransfer2);
        when(transferServiceMock.getCurrentUserTransfers()).thenReturn(displayingTransferList);
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.get("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers"))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("type", is("debit")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(-50.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(150.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer Montepio")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("type", is("credit")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer payMyBuddy")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(100.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(200.0)))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsCredit_thenReturnBalanceCreditedWithAmount() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("amount", String.valueOf(50.0))
                                .param("type", "credit")
                ).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(50.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("type", is("credit"))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsEnough_thenReturnNewBalanceDebitedfromAmount() throws Exception {
        //GIVEN
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("amount", String.valueOf(80.0))
                                .param("type", "debit")
                                .param("balance", String.valueOf(100.0))
//                        .param("userEmail", SecurityUtilities.userEmail))
                ).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(80.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("type", is("debit"))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsInsufficient_thenThrowBalanceInsufficientException() throws Exception {
        //GIVEN
        when(transferServiceMock.addTransfer(isA(DisplayingTransfer.class))).thenThrow(new BalanceInsufficientException("Balance insufficient for execute the transfer"));
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("amount", String.valueOf(180.0))
                                .param("type", "debit")
                                .param("balance", String.valueOf(100.0))
//                        .param("userEmail", SecurityUtilities.userEmail))
                ).andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(180.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("type", is("debit"))))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "BalanceInsufficient"))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenAmountIsNull_thenReturnFieldsErrorsNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("type", "credit"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "NotNull"))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenAmountIsLessTo1_thenReturnFieldsErrorsMin() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "0.0")
                        .param("type", "credit"))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "Min"))
                .andDo(print());
    }


    @Test
    public void addTransferCurrentUserTest_whenValueSelectorTypeIsBlank_thenReturnFieldsErrorsNotBlank() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "20")
                        .param("type", ""))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "type", "NotBlank"))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenValueSelectorTypeIsBlankAndAmountIsNull_thenReturnFieldsErrorsNotBlankAndNotNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("amount", "")
                        .param("type", ""))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "amount", "NotNull"))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "type", "NotBlank"))
                .andDo(print());
    }
}
