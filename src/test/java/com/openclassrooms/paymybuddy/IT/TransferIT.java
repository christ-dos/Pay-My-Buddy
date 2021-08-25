package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
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
public class TransferIT {

    @Autowired
    private MockMvc mockMvcTransfer;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvcTransfer = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /*-----------------------------------------------------------------------------------------------------
                                       Integration tests View transfer
    ---------------------------------------------------------------------------------------------------------*/
    @Test
    public void getCurrentUserTransfersTest_whenUrlIsSlashTransfer_thenReturnTwoModelsAndStatusOk() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcTransfer.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().size(6))
                .andExpect(model().attributeExists("displayingTransfer", "transfers","transferTypes","totalPages", "currentPage"))
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
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.get("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("transferType", is(TransferTypeEnum.CREDIT)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(20.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(209.95)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer appli")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("transferType", is(TransferTypeEnum.DEBIT)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("description", is("transfer la Poste")))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("amount", is(-50.0)))))
                .andExpect(model().attribute("transfers", hasItem(hasProperty("postTradeBalance", is(159.95)))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsCredit_thenReturnBalanceCreditedWithAmount() throws Exception {
        //WHEN
        //THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("userEmail", SecurityUtilities.currentUser)
                        .param("amount", String.valueOf(20.0))
                        .param("description", "transfer appli")
                        .param("transferType", TransferTypeEnum.CREDIT.name())
                        .param("postTradeBalance", String.valueOf(220.0))
                ).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(20.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.CREDIT))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("description", is("transfer appli"))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("postTradeBalance", is(220.0))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsEnough_thenReturnTransferAddedWithUserWithNewBalanceDebitedfromAmount() throws Exception {
        //GIVEN
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("amount", String.valueOf(50.0))
                                .param("transferType", TransferTypeEnum.DEBIT.name())
//                                .param("balance", String.valueOf(100.0))
                                .param("postTradeBalance", String.valueOf(170))
                                .param("description", "transfer la Poste")
//                        .param("userEmail", SecurityUtilities.userEmail))
                ).andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(50.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.DEBIT))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("postTradeBalance", is(170.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("description", is("transfer la Poste"))))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenTransferTypeIsDebitAndBalanceIsInsufficient_thenThrowBalanceInsufficientException() throws Exception {
        //GIVEN
        // WHEN
        // THEN
        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("amount", String.valueOf(580.0))
                                .param("transferType", TransferTypeEnum.DEBIT.name())
                                .param("balance", String.valueOf(100.0))
//                        .param("userEmail", SecurityUtilities.userEmail))
                ).andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("displayingTransfer", hasProperty("amount", is(580.0))))
                .andExpect(model().attribute("displayingTransfer", hasProperty("transferType", is(TransferTypeEnum.DEBIT))))
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
                        .param("transferType", TransferTypeEnum.DEBIT.name()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
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
                        .param("transferType", TransferTypeEnum.CREDIT.name()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
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
                        .param("transferType", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("displayingTransfer", "transfers", "transferTypes"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("displayingTransfer", "transferType", "NotNull"))
                .andDo(print());
    }

    @Test
    public void addTransferCurrentUserTest_whenValueSelectorTypeIsBlankAndAmountIsNull_thenReturnFieldsErrorsNotBlankAndNotNull() throws Exception {
        //GIVEN
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
