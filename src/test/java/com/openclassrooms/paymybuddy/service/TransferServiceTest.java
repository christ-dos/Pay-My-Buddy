package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private ITransferRepository transferRepositoryMock;

    @Mock
    private IUserRepository userRepositoryMock;

    private TransferService transferServiceTest;

    @BeforeEach
    public void setPerTest() {

        transferServiceTest = new TransferService(transferRepositoryMock, userRepositoryMock);
    }


    @Test
    public void addTransferTest_whenUserEmailIsDada_thenVerifyBalanceIs120(){
        //GIVEN
        User user = User.builder()
                .email("dada@email.fr").firstName("Damien").lastName("Sanches").balance(20.0).accountBank(589632)
                .build();
        Transfer transferTest = Transfer.builder().transferId(1)
                .amount(100.0).date(LocalDateTime.now()).type("credit").userEmail("dada@email.fr").build();
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferTest);
        when(userRepositoryMock.findByEmail(transferTest.getUserEmail())).thenReturn(user);
        //WHEN
        Transfer transferResult = transferServiceTest.addTransfer(transferTest);
        //THEN
        assertEquals("dada@email.fr", transferResult.getUserEmail());
        assertEquals(100,transferResult.getAmount());
        assertEquals("credit", transferResult.getType());
        assertEquals(589632,transferResult.getUser().getAccountBank());
        assertEquals(120.0, transferResult.getUser().getBalance());
    }
}
