package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        String userEmail = SecurityUtilities.userEmail;
        User user = User.builder()
                .email(userEmail).firstName("Damien").lastName("Sanchez").balance(20.0).accountBank(589632)
                .build();

        User userWithBalanceUpdated = User.builder()
                .email(userEmail).firstName("Damien").lastName("Sanchez").balance(120.0).accountBank(589632)
                .build();
        DisplayingTransfer displayingTransferCredit = new DisplayingTransfer();
        displayingTransferCredit.setAmount(100.0);
        displayingTransferCredit.setTransferType(TransferTypeEnum.CREDIT);
        displayingTransferCredit.setDescription("credit payMyBuddy");
        Transfer transferToAdd = Transfer.builder().transferId(1)
                .amount(100.0).date(LocalDateTime.now()).transferType(TransferTypeEnum.CREDIT).user(userWithBalanceUpdated).build();
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferToAdd);
        when(userRepositoryMock.findByEmail(transferToAdd.getUser().getEmail())).thenReturn(user);
        //WHEN
        Transfer transferResult = transferServiceTest.addTransfer(displayingTransferCredit);
        //THEN
        assertEquals(userEmail, transferResult.getUser().getEmail());
        assertEquals(100, transferResult.getAmount());
        assertEquals(TransferTypeEnum.CREDIT, transferResult.getTransferType());
        assertEquals(589632, transferResult.getUser().getAccountBank());
        assertEquals(120.0, transferResult.getUser().getBalance());
        verify(transferRepositoryMock, times(1)).save(isA(Transfer.class));
    }

    @Test
    public void addTransferTest_whenTransferTypeIsDebitAndBalanceIsEnough_thenTransferIsSavedAndBalanceUpdatedTo50() {
        //GIVEN
        String userEmail = SecurityUtilities.userEmail;
        User user = User.builder()
                .email(userEmail).firstName("Damien").lastName("Sanchez").balance(100.0).accountBank(589632)
                .build();
        DisplayingTransfer displayingTransferDebit = new DisplayingTransfer();
        displayingTransferDebit.setTransferType(TransferTypeEnum.DEBIT);
        displayingTransferDebit.setAmount(50.0);
        displayingTransferDebit.setDescription("transfer to BNP");

        User userBalanceUpdated = User.builder()
                .email(userEmail).firstName("Damien").lastName("Sanchez").balance(50.0).accountBank(589632)
                .build();

        Transfer transferTestDebit = Transfer.builder().transferId(1)
                .amount(50.0).date(LocalDateTime.now()).transferType(TransferTypeEnum.DEBIT).description("transfer to BNP").user(userBalanceUpdated).build();
        when(transferRepositoryMock.save(isA(Transfer.class))).thenReturn(transferTestDebit);
        when(userRepositoryMock.findByEmail(transferTestDebit.getUser().getEmail())).thenReturn(user);
        //WHEN
        Transfer transferResultDebitBalanceEnough = transferServiceTest.addTransfer(displayingTransferDebit);
        //THEN
        //new balance is updated
        assertEquals(TransferTypeEnum.DEBIT, transferResultDebitBalanceEnough.getTransferType());
        assertEquals(50, transferResultDebitBalanceEnough.getUser().getBalance());
        assertEquals(userEmail, transferTestDebit.getUser().getEmail());
        verify(transferRepositoryMock, times(1)).save(isA(Transfer.class));
    }

    @Test
    public void addTransferTest_whenTransferTypeIsDebitAndBalanceInsufficient_thenTrowBalanceInsufficientException() {
        //GIVEN
        String userEmail = SecurityUtilities.userEmail;
        User user = User.builder()
                .email(userEmail).firstName("Damien").lastName("Sanchez").balance(49.0).accountBank(589632)
                .build();
        DisplayingTransfer displayingTransferDebitBalanceInsufficient = new DisplayingTransfer();
        displayingTransferDebitBalanceInsufficient.setTransferType(TransferTypeEnum.DEBIT);
        displayingTransferDebitBalanceInsufficient.setAmount(50.0);
        displayingTransferDebitBalanceInsufficient.setDescription("transfer to BNP");

        Transfer transferTestDebitBalanceInsufficient = Transfer.builder().transferId(1)
                .amount(50.0).date(LocalDateTime.now()).transferType(TransferTypeEnum.DEBIT).description("transfer to BNP").user(user).build();
        when(userRepositoryMock.findByEmail(transferTestDebitBalanceInsufficient.getUser().getEmail())).thenReturn(user);
        //WHEN
        //THEN
        assertThrows(BalanceInsufficientException.class, () -> transferServiceTest.addTransfer(displayingTransferDebitBalanceInsufficient));
        verify(transferRepositoryMock, times(0)).save(transferTestDebitBalanceInsufficient);
    }

    @Test
    public void getCurrentUserTransfersTest_whenCurrentUserIsDada_thenReturnListDisplayingTransferForDadaWithSignNegativeIfTypeIsDebit(){
        //GIVEN
        String userEmail = SecurityUtilities.userEmail;
        User userTransfer1 = User.builder()
                .email(userEmail)
                .firstName("Damien")
                .lastName("Sanchez")
                .balance(120.0)
                .build();

        User userTransfer2 = User.builder()
                .email(userEmail)
                .firstName("Damien")
                .lastName("Sanchez")
                .balance(100.0)
                .build();

         List<Transfer> transfersList = new ArrayList<>();
         Transfer transfer1  = Transfer.builder()
                         .amount(20.0).description("transfer PayMyBuddy").transferType(TransferTypeEnum.CREDIT).user(userTransfer1).build();
        Transfer transfer2  = Transfer.builder()
                .amount(30.0).description("transfer BNP").transferType(TransferTypeEnum.DEBIT).user(userTransfer2).build();

        transfersList.add(transfer1);
        transfersList.add(transfer2);

////        when(transferRepositoryMock.findTransfersByUserEmailOrderByDateDesc(isA(String.class),isA(Pageable.class))).thenReturn(transfersList);
////        //WHEN
////        List<DisplayingTransfer> displayingTransfersResult = transferServiceTest.getCurrentUserTransfers();
//        //THEN
//        assertEquals(2,displayingTransfersResult.size());
//        assertEquals(TransferTypeEnum.CREDIT,displayingTransfersResult.get(0).getTransferType());
//        assertEquals(+20,displayingTransfersResult.get(0).getAmount());
//        assertEquals(TransferTypeEnum.DEBIT,displayingTransfersResult.get(1).getTransferType());
//        assertEquals(-30,displayingTransfersResult.get(1).getAmount());
    }

    @Test
    void getCurrentUserTransfersTest_whenCurrentUserTransferNotExist_thenReturnNull() {
        //GIVEN
//        List<DisplayingTransaction> emptyListTransfer = new ArrayList<>();
//        //WHEN
//        List<DisplayingTransfer> transfersResult = transferServiceTest.getCurrentUserTransfers();
//        //THEN
//        assertEquals(emptyListTransfer, transfersResult);
    }

}
