package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

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
    public void addTransferTest_whenUserEmailIsDadaAndTransferTypeIsCredit_thenVerifyBalanceIs120AndUserEmailIsDada() {
        //GIVEN
        User user = User.builder()
                .email("dada@email.fr").firstName("Damien").lastName("Sanchez").balance(20.0).accountBank(589632)
                .build();
        Transfer transferTestCredit = Transfer.builder().transferId(1)
                .amount(100.0).date(LocalDateTime.now()).type("credit").userEmail("dada@email.fr").build();
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferTestCredit);
        when(userRepositoryMock.findByEmail(transferTestCredit.getUserEmail())).thenReturn(user);
        //WHEN
        Transfer transferResult = transferServiceTest.addTransfer(transferTestCredit);
        //THEN
        assertEquals("dada@email.fr", transferResult.getUserEmail());
        assertEquals(100, transferResult.getAmount());
        assertEquals("credit", transferResult.getType());
        assertEquals(589632, transferResult.getUser().getAccountBank());
        assertEquals(120.0, transferResult.getUser().getBalance());
        verify(transferRepositoryMock, times(1)).save(isA(Transfer.class));
    }

    @Test
    public void addTransferTest_whenTransferTypeIsDebitAndBalanceIsEnough_thenTransferIsSavedAndBalanceUpdatedTo50() {
        //GIVEN
        User user = User.builder()
                .email("dada@email.fr").firstName("Damien").lastName("Sanchez").balance(100.0).accountBank(589632)
                .build();
        Transfer transferTestDebit = Transfer.builder().transferId(1)
                .amount(50.0).date(LocalDateTime.now()).type("debit").userEmail("dada@email.fr").build();
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferTestDebit);
        when(userRepositoryMock.findByEmail(transferTestDebit.getUserEmail())).thenReturn(user);
        //WHEN
        Transfer transferResultDebitBalanceEnough = transferServiceTest.addTransfer(transferTestDebit);
        //THEN
        //new balance is updated
        assertEquals("debit", transferResultDebitBalanceEnough.getType());
        assertEquals(50, transferResultDebitBalanceEnough.getUser().getBalance());
        assertEquals("dada@email.fr", transferTestDebit.getUserEmail());
        verify(transferRepositoryMock, times(1)).save(isA(Transfer.class));
    }

    @Test
    public void addTransferTest_whenTransferTypeIsDebitAndBalanceInsufficient_thenTrowBalanceInsufficientException() {
        //GIVEN
        User user = User.builder()
                .email("dada@email.fr").firstName("Damien").lastName("Sanchez").balance(49.0).accountBank(589632)
                .build();
        Transfer transferTestDebitBalanceInsufficient = Transfer.builder().transferId(1)
                .amount(50.0).date(LocalDateTime.now()).type("debit").userEmail("dada@email.fr").build();
        when(userRepositoryMock.findByEmail(transferTestDebitBalanceInsufficient.getUserEmail())).thenReturn(user);
        //WHEN
        //THEN
        assertThrows(BalanceInsufficientException.class, () -> transferServiceTest.addTransfer(transferTestDebitBalanceInsufficient));
        verify(transferRepositoryMock, times(0)).save(transferTestDebitBalanceInsufficient);
    }

}
