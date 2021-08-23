package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    ITransactionService transactionServiceTest;

    @Mock
    ITransactionRepository transactionRepositoryMock;

    @Mock
    IUserRepository userRepositoryMock;

    private Pageable pageable;

    private List<Transaction> transactions;

    private Page<Transaction> transactionsPage;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 5);
        transactionServiceTest = new TransactionService(transactionRepositoryMock, userRepositoryMock);

        String emitterEmail = SecurityUtilities.userEmail;

        transactions = new ArrayList<>();
        Transaction transaction1 = Transaction.builder()
                .transactionId(1).amount(25.0).description("diner paula")
                .userEmitter(User.builder().email(emitterEmail).build()).userReceiver(User.builder().email("lisa@email.fr").build())
                .date(LocalDateTime.now())
                .build();
        Transaction transaction2 = Transaction.builder()
                .transactionId(2).amount(15.0).description("shopping  casa china")
                .userEmitter(User.builder().email(emitterEmail).build())
                .userReceiver(User.builder().email("lisa@email.fr").build())
                .date(LocalDateTime.now())
                .build();
        Transaction transaction3 = Transaction.builder()
                .transactionId(3).amount(18.0).description("movies tickets")
                .userEmitter(User.builder().email("lisa@email.fr").build())
                .userReceiver(User.builder().email(emitterEmail).build())
                .date(LocalDateTime.now())
                .build();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        transactionsPage = new PageImpl<>(transactions);
    }

    @Test
    void getTransactionsTest_thenReturnIterableContainingThreeTransactions() {
        //GIVEN
        int count = 0;
        when(transactionRepositoryMock.findAll()).thenReturn(transactions);
        //WHEN
        Iterable<Transaction> transactionsResult = transactionServiceTest.getTransactions();
        Iterator<Transaction> it = transactionsResult.iterator();
        //method that count the iterable
        while (it.hasNext()) {
            it.next();
            count++;
        }
        //THEN
        //verify that transactionsResult contain 3 elements
        assertEquals(3, count);
        assertEquals(transactions, transactionsResult);
        verify(transactionRepositoryMock, times(1)).findAll();
    }

    @Test
    void getCurrentUserTransactionsByEmailTest_whenTransactionWithEmailEmitterIsDadaOrReceiverEmailIsDada_thenReturnListDisplayingTransactionForEmailDadaWithSignNegativeIfEmitterEmailIsDada() {
        //GIVEN
        User userLise = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("Lisette")
                .lastName("Dumarche")
                .balance(30.50)
                .accountBank(170974).build();

        when(transactionRepositoryMock.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(isA(String.class), isA(String.class), isA(Pageable.class))).thenReturn(transactionsPage);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userLise);
        //WHEN
        Page<DisplayingTransaction> listTransactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail(pageable);
        //THEN
        assertEquals(3, listTransactionsResult.stream().count());
        assertEquals("Lisette", listTransactionsResult.getContent().get(0).getFirstName());
        assertEquals(-25, listTransactionsResult.getContent().get(0).getAmount());
        assertEquals("Lisette", listTransactionsResult.getContent().get(2).getFirstName());
        assertEquals(18, listTransactionsResult.getContent().get(2).getAmount());
        verify(userRepositoryMock, times(3)).findByEmail(isA(String.class));
    }

    @Test
    void getCurrentUserTransactionsByEmailTest_whenEmitterEmailTransactionNotExist_thenReturnNull() {
        //GIVEN
        User userLise = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("Lisette")
                .lastName("Dumarche")
                .balance(30.50)
                .accountBank(170974).build();

        List<Transaction> emptyList = new ArrayList<>();
        Page<Transaction> displayingTransactionsPageEmpty = new PageImpl<>(emptyList);
        when(transactionRepositoryMock.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(isA(String.class), isA(String.class), isA(Pageable.class))).thenReturn(displayingTransactionsPageEmpty);
        //WHEN
        Page<DisplayingTransaction> transactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail(pageable);
        //THEN
        //if the list is empty assert true
        assertTrue(transactionsResult.isEmpty());
    }

    @Test
    public void addTransaction_whenUserBalanceIsSufficient_thenVerifyTransactionAdded() {
        //GIVEN
        User userEmitter = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(100.50)
                .accountBank(170974).build();

        User userReceiver = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("lisette")
                .lastName("Duhamel")
                .balance(10.0)
                .accountBank(170974).build();

        SendTransaction sendTransaction = new SendTransaction("lisa@email.fr", 100.0, "books");

        Transaction transactionTest = Transaction.builder().transactionId(1)
                .userEmitter(userEmitter)
                .userReceiver(User.builder().email("lisa@email.fr").build())
                .amount(100.0).fees(0.50)
                .date(LocalDateTime.now()).build();

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userEmitter, userReceiver);
        when(transactionRepositoryMock.save(isA(Transaction.class))).thenReturn(transactionTest);
        //WHEN
        Transaction transactionResult = transactionServiceTest.addTransaction(sendTransaction);
        //THEN
        assertEquals("kikine@email.fr", transactionResult.getUserEmitter().getEmail());
        assertEquals("lisa@email.fr", transactionResult.getUserReceiver().getEmail());
        assertEquals(100, transactionResult.getAmount());
        assertEquals(0.5, transactionResult.getFees());
        verify(transactionRepositoryMock, times(1)).save(isA(Transaction.class));
    }

    @Test
    public void addTransaction_whenUserBalanceNotEnough_thenThrowBalanceInsufficientException() {
        //GIVEN
        User emitter = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(200.0)
                .accountBank(170974).build();

        SendTransaction sendTransaction = new SendTransaction("lisa@email.fr", 250.0, "books");

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(emitter);
        //WHEN
        //THEN
        assertThrows(BalanceInsufficientException.class, () -> transactionServiceTest.addTransaction(sendTransaction));
        verify(userRepositoryMock, times(1)).findByEmail(isA(String.class));
    }


}