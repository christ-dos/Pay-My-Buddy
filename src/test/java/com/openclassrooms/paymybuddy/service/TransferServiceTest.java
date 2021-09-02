package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtilities.class)
public class TransferServiceTest {

    @Mock
    private ITransferRepository transferRepositoryMock;

    @Mock
    private IUserRepository userRepositoryMock;

    private TransferService transferServiceTest;

    String username;


    @BeforeEach
    public void setPerTest() {

        transferServiceTest = new TransferService(transferRepositoryMock, userRepositoryMock);
    }

    @Test
    public void addTransferTest_whenUserEmailIsDadaAndTransferTypeIsCredit_thenVerifyBalanceIs120AndUserEmailIsDada() {
        //GIVEN
        String userEmail = username;
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
        PowerMockito.mockStatic(SecurityUtilities.class);
        when(SecurityUtilities.getCurrentUser()).thenReturn("dada@email.fr");
        String userEmail = SecurityUtilities.getCurrentUser();
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
        String userEmail = SecurityUtilities.getCurrentUser();
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
    public void getCurrentUserTransfersTest_whenCurrentUserIsDada_thenReturnPageOfDisplayingTransferForDadaWithSignNegativeIfTypeIsDebit() {
        //GIVEN
        String userEmail = "dada@email.fr";
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

        Pageable pageable = PageRequest.of(0, 5);

        Transfer transfer1 = Transfer.builder()
                .amount(20.0).description("transfer PayMyBuddy").transferType(TransferTypeEnum.CREDIT).user(userTransfer1).build();
        Transfer transfer2 = Transfer.builder()
                .amount(30.0).description("transfer BNP").transferType(TransferTypeEnum.DEBIT).user(userTransfer2).build();
        List<Transfer> transferList = new ArrayList<>();
        transferList.add(transfer1);
        transferList.add(transfer2);
        Page<Transfer> transferPage = new PageImpl<>(transferList);

        when(transferRepositoryMock.findTransfersByUserEmailOrderByDateDesc(isA(String.class), isA(Pageable.class))).thenReturn(transferPage);
        //WHEN
        Page<DisplayingTransfer> displayingTransfersResult = transferServiceTest.getCurrentUserTransfers(pageable);
        //THEN
        assertEquals(2, displayingTransfersResult.stream().count());
        assertEquals(TransferTypeEnum.CREDIT, displayingTransfersResult.getContent().get(0).getTransferType());
        assertEquals(+20, displayingTransfersResult.getContent().get(0).getAmount());
        assertEquals(TransferTypeEnum.DEBIT, displayingTransfersResult.getContent().get(1).getTransferType());
        assertEquals(-30, displayingTransfersResult.getContent().get(1).getAmount());
    }

    @Test
    void getCurrentUserTransfersTest_whenCurrentUserTransferNotExist_thenReturnAPageEmpty() {
        //GIVEN
        Pageable pageable = PageRequest.of(0,5);
        List<Transfer> displayingTransferList = new ArrayList<>();
        Page<Transfer> displayingTransferPageEmpty = new PageImpl<>(displayingTransferList);
        when(transferRepositoryMock.findTransfersByUserEmailOrderByDateDesc(isA(String.class), isA(Pageable.class))).thenReturn(displayingTransferPageEmpty);
        //WHEN
        Page<DisplayingTransfer> transfersResult = transferServiceTest.getCurrentUserTransfers(pageable);
        //THEN
        //verify that the page is empty
        assertTrue(transfersResult.getContent().isEmpty());
    }

}
