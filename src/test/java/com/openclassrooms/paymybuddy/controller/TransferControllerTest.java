package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void addTransferCurrentUser_whenTransferIsCredit_thenReturnBalanceCredited() throws Exception {
        //GIVEN
        User user = User.builder()
                .email("dada@email.fr").firstName("Damien").lastName("Sanchez").balance(100.0).accountBank(589632)
                .build();
        Transfer transferTestCredit = Transfer.builder().transferId(1)
                .amount(50.0).date(LocalDateTime.now()).type("debit").userEmail("dada@email.fr").build();
        when(transferServiceMock.addTransfer(isA(Transfer.class))).thenReturn(transferTestCredit);
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferTestCredit);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(user);
        //WHEN
        //THEN

        mockMvcTransfer.perform(MockMvcRequestBuilders.post("/transfer").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "dada@email.fr")
                        .param("firstName", "Damien")
                        .param("lastName", "Sanchez")
                        .param("userEmail", SecurityUtilities.userEmail))
                .andExpect(status().isOk()).andDo(print());
    }
}
